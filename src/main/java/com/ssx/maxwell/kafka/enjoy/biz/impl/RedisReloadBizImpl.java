package com.ssx.maxwell.kafka.enjoy.biz.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ssx.maxwell.kafka.enjoy.biz.RedisReloadBiz;
import com.ssx.maxwell.kafka.enjoy.common.helper.RedissonHelper;
import com.ssx.maxwell.kafka.enjoy.common.helper.StringRedisTemplateHelper;
import com.ssx.maxwell.kafka.enjoy.common.model.bo.RedisMappingBO;
import com.ssx.maxwell.kafka.enjoy.common.model.dto.CacheListDTO;
import com.ssx.maxwell.kafka.enjoy.common.model.entity.RedisMapping;
import com.ssx.maxwell.kafka.enjoy.common.tools.*;
import com.ssx.maxwell.kafka.enjoy.configuration.DynamicDsInfo;
import com.ssx.maxwell.kafka.enjoy.configuration.ServiceBeanDefinitionRegistry;
import com.ssx.maxwell.kafka.enjoy.enumerate.MaxwellBinlogConstants;
import com.ssx.maxwell.kafka.enjoy.service.RedisMappingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/12 13:47
 * @description: no
 */
@Service
@Slf4j
public class RedisReloadBizImpl implements RedisReloadBiz {
    @Autowired
    private RedissonHelper redissonHelper;
    @Autowired
    private RedisMappingService redisMappingService;
    @Value("${spring.profiles.active:dev}")
    private String profile;
    @Autowired
    private SpringContextUtils springContextUtils;
    @Autowired
    private StringRedisTemplateHelper stringRedisTemplateHelper;

    @Override
    public boolean reloadCache(String dbDatabase, String dbTable, Long dbPid) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(dbDatabase).append(dbTable);
        redissonHelper.lock(stringBuilder.toString(), 10, 5, TimeUnit.SECONDS, () -> {
            RedisMappingBO redisMappingBO = new RedisMappingBO();
            redisMappingBO.setDbDatabase(dbDatabase).setDbTable(dbTable);
            RedisMapping redisMapping = redisMappingService.queryOneByDatabaseAndTable(redisMappingBO);
            if (null != redisMapping) {
                String rule = redisMapping.getRule();
                if (!Strings.isNullOrEmpty(rule) && !MaxwellBinlogConstants.REDIS_RULE_0.equals(rule)) {
                    String[] ruleArr = rule.split(",");
                    if (ArrayUtils.isNotEmpty(ruleArr)) {
                        if (ArrayUtils.contains(ruleArr, MaxwellBinlogConstants.REDIS_RULE_1)) {
                            //单表主键缓存
                            String jdbcSql = MessageFormat.format(MaxwellBinlogConstants.RedisRunSqlTemplateEnum.SQL_PRIMARY_ID.getTemplate(), dbTable, dbPid);
                            String redisKey = MessageFormat.format(MaxwellBinlogConstants.RedisCacheKeyTemplateEnum.REDIS_CACHE_KEY_TEMPLATE_ITEM_PK_ID.getTemplate(), profile, dbDatabase, dbTable, dbPid);
                            log.info("sql= {} , key= {}", jdbcSql, redisKey);
                            executeToRedis(loopGetDynamicDsInfo(dbDatabase), jdbcSql, redisKey, redisMapping, false, redisMapping.getPrimaryExpire());
                        }
                        if (ArrayUtils.contains(ruleArr, MaxwellBinlogConstants.REDIS_RULE_2)) {
                            //全表缓存
                            String jdbcSql = MessageFormat.format(MaxwellBinlogConstants.RedisRunSqlTemplateEnum.SQL_ALL.getTemplate(), dbTable);
                            String redisKey = MessageFormat.format(MaxwellBinlogConstants.RedisCacheKeyTemplateEnum.REDIS_CACHE_KEY_TEMPLATE_PREFIX_LIST.getTemplate(), profile, dbDatabase, dbTable);
                            log.info("sql= {} , key= {}", jdbcSql, redisKey);
                            executeToRedis(loopGetDynamicDsInfo(dbDatabase), jdbcSql, redisKey, redisMapping, false, redisMapping.getTableExpire());
                        }
                        if (ArrayUtils.contains(ruleArr, MaxwellBinlogConstants.REDIS_RULE_3)) {
                            //自定义缓存
                            String jdbcSql = MessageFormat.format(MaxwellBinlogConstants.RedisRunSqlTemplateEnum.SQL_PRIMARY_ID.getTemplate(), dbTable, dbPid);
                            String redisKey = MessageFormat.format(MaxwellBinlogConstants.RedisCacheKeyTemplateEnum.REDIS_CACHE_KEY_TEMPLATE_PREFIX_CUSTOM.getTemplate(), profile, dbDatabase, dbTable);
                            log.info("sql= {} , key= {}", jdbcSql, redisKey);
                            executeToRedis(loopGetDynamicDsInfo(dbDatabase), jdbcSql, redisKey, redisMapping, true, Long.MAX_VALUE);

                        }
                    }
                }
            }
        });
        return false;
    }

    /**
     * 功能描述: 获取数据源相关信息
     *
     * @param: [dbDatabase]
     * @return: com.ssx.maxwell.kafka.enjoy.configuration.DynamicDsInfo
     * @author: shuaishuai.xiao
     * @date: 2019/6/14 16:57
     */
    private DynamicDsInfo loopGetDynamicDsInfo(String dbDatabase) {
        List<DynamicDsInfo> dynamicDsInfoList = ServiceBeanDefinitionRegistry.DYNAMICDSINFO_LIST;
        if (null == dynamicDsInfoList || dynamicDsInfoList.isEmpty()) {
            log.error("动态数据源BIZ加载失败、bean集合为空");
        }
        for (DynamicDsInfo d : dynamicDsInfoList) {
            if (null != d && dbDatabase.equals(d.getDatabase())) {
                return d;
            }
        }
        return null;
    }

    /**
     * 功能描述: 执行至redis业务
     *
     * @param: [dynamicDsInfo, sql, redisKey, redisMapping, isCustom, rule]
     * @return: void
     * @author: shuaishuai.xiao
     * @date: 2019/6/14 17:31
     */
    private void executeToRedis(DynamicDsInfo dynamicDsInfo, String sql, String redisKey, RedisMapping redisMapping, boolean isCustom, Long expire) {

        if (isCustom) {
            //自定义缓存
            String template = redisMapping.getTemplate();
            log.info("自定义缓存, template={}", template);
            if (!Strings.isNullOrEmpty(template)) {
                List<Map<String, Object>> dbDataList = queryDbList(dynamicDsInfo, sql);
                if (null == dbDataList || dbDataList.isEmpty()) {
                    log.warn("自定义缓存查询数据库集合为空, 不进行构建, redisMapping={}", redisMapping);
                } else {
                    String[] templateArr = template.split(",");
                    if (null != templateArr && templateArr.length > 0) {
                        //按照主键ID确保只有一个值返回, 如果一个主键查到多个返回值、检查应用健康情况
                        Map<String, Object> stringObjectMap = dbDataList.get(0);
                        for (String part : templateArr) {
                            //:order_code:is_del(3600),
                            //:goods_name:is_del(-1)
                            String[] a2 = part.split(":");
                            StringBuilder keyBuilder = new StringBuilder();
                            for (int k=0 ;k < a2.length; k++) {
                                String field = null;
                                if(k == a2.length - 1){
                                    //：最后一位过滤掉(过期时间)这部分内容
                                    field = a2[k].substring(0, a2[k].indexOf("("));
                                }else {
                                    field = a2[k];
                                }
                                if (!Strings.isNullOrEmpty(field)) {
                                    keyBuilder.append(":");
                                    Object dbObj = stringObjectMap.get(field);
                                    if (null == dbObj || "".equals(dbObj)) {
                                        keyBuilder.append(MaxwellBinlogConstants.REDIS_VAL_NULL_MAGIC);
                                    } else {
                                        if (dbObj instanceof String) {
                                            //字符串判断是否包含中文
                                            if (PatternUtils.isContainChinese((String) dbObj)) {
                                                //转码
                                                keyBuilder.append(UnicodeUtils.cnToUnicode((String) dbObj));
                                            } else {
                                                keyBuilder.append(dbObj);
                                            }
                                        } else {
                                            keyBuilder.append(dbObj);
                                        }
                                    }
                                }
                            }
                            //获取自定义缓存过期时间
                            expire = Long.valueOf(part.substring(part.indexOf("(")+1,part.indexOf(")")));
                            String finalKey = keyBuilder.insert(0, redisKey).toString();
                            log.info("自定义缓存, key={}, expire={}", finalKey, expire);
                            setValueToRedis(redisMapping, finalKey, buildCacheListDTO(dbDataList), expire);
                        }
                    }
                }

            }
        } else {
            List<Map<String, Object>> dbDataList = queryDbList(dynamicDsInfo, sql);
            if (null == dbDataList || dbDataList.isEmpty()) {
                //处理DB查询无数据情况
                CacheListDTO cacheListDTO = new CacheListDTO().setNone(true);
                cacheListDTO.setObj(Lists.newArrayList());
                setValueToRedis(redisMapping, redisKey, cacheListDTO, expire);
            } else {
                setValueToRedis(redisMapping, redisKey, buildCacheListDTO(dbDataList), expire);
            }
        }

    }

    /**
     * 功能描述: 执行数据库查询
     *
     * @param: [dynamicDsInfo, sql]
     * @return: java.util.List<java.util.Map                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               <                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               java.lang.String                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               ,                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               java.lang.Object>>
     * @author: shuaishuai.xiao
     * @date: 2019/6/14 17:30
     */
    private List<Map<String, Object>> queryDbList(DynamicDsInfo dynamicDsInfo, String sql) {
        try {
            Object object = springContextUtils.getBean(dynamicDsInfo.getBizBeanName());
            Class cls = object.getClass();
            Method method = cls.getMethod(DynGenerateClassUtils.BIZ_DEFAULT_METHOD_NAME, new Class[]{String.class});
            List<Map<String, Object>> dbDataList = (List<Map<String, Object>>) method.invoke(object, sql);
            log.info("数据库源数据返回,dynamicDsInfo={}, sql={},list={}", dynamicDsInfo, sql, dbDataList);
            return dbDataList;
        } catch (Exception e) {
            log.error("反射执行BIZ出错,e=", e);
        }
        return null;
    }

    /**
     * 功能描述: set to redis
     *
     * @param: [redisMapping, redisKey, cacheListDTO, expire]
     * @return: void
     * @author: shuaishuai.xiao
     * @date: 2019/6/14 17:29
     */
    private void setValueToRedis(RedisMapping redisMapping, String redisKey, CacheListDTO cacheListDTO, Long expire) {
        try {
            if (expire.equals(-1)) {
                stringRedisTemplateHelper.set(redisKey, cacheListDTO);
            } else {
                stringRedisTemplateHelper.set(redisKey, cacheListDTO, expire, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            log.error("redis存值出错, e={}", e);
        }
    }

    /**
     * 功能描述:CacheListDTO生成
     *
     * @param: [dbDataList]
     * @return: com.ssx.maxwell.kafka.enjoy.common.model.dto.CacheListDTO
     * @author: shuaishuai.xiao
     * @date: 2019/6/14 17:30
     */
    private CacheListDTO buildCacheListDTO(List<Map<String, Object>> dbDataList) {
        CacheListDTO cacheListDTO = new CacheListDTO().setNone(false);
        List<Map<String, Object>> cacheList = new ArrayList(dbDataList.size());
        for (Object dbObj : dbDataList) {
            Map<String, Object> map = (Map<String, Object>) dbObj;
            Map<String, Object> cacheMap = Maps.newHashMap();
            Set<String> fieldSet = map.keySet();
            for (String field : fieldSet) {
                cacheMap.put(JsonUtils.lineToHump(field), map.get(field));
            }
            cacheList.add(cacheMap);
        }
        cacheListDTO.setObj(cacheList);
        return cacheListDTO;
    }

    public static void main(String[] args) {
        String a = ":order_code:is_del,:goods_name:is_del";
        String[] a1 = a.split(",");
        System.out.println(a1);
        for (String s : a1) {
            System.out.println(s);
            String[] a2 = s.split(":");
            for (String f : a2) {
                if (!Strings.isNullOrEmpty(f)) {
                    System.out.println("f=====" + f);
                }
            }
        }
    }

}
