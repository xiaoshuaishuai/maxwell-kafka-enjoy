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
import com.ssx.maxwell.kafka.enjoy.common.tools.DynGenerateClassUtils;
import com.ssx.maxwell.kafka.enjoy.common.tools.JsonUtils;
import com.ssx.maxwell.kafka.enjoy.common.tools.SpringContextUtils;
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
                            String redisKey = MessageFormat.format(MaxwellBinlogConstants.RedisCacheKeyTemplateEnum.REDIS_CACHE_KEY_TEMPLATE_ITEM_PKID.getTemplate(), profile, dbDatabase, dbTable, dbPid);
                            log.info("sql= {} , key= {}", jdbcSql, redisKey);
                            //todo 2019-6-12 17:47:49 动态执行sql语句并将结果集放入缓存
                            executeSql(loopGetDynamicDsInfo(dbDatabase), jdbcSql, redisKey, redisMapping);
                        }
                        if (ArrayUtils.contains(ruleArr, MaxwellBinlogConstants.REDIS_RULE_2)) {
                            //全表缓存
                            String jdbcSql = MessageFormat.format(MaxwellBinlogConstants.RedisRunSqlTemplateEnum.SQL_ALL.getTemplate(), dbTable);
                            String redisKey = MessageFormat.format(MaxwellBinlogConstants.RedisCacheKeyTemplateEnum.REDIS_CACHE_KEY_TEMPLATE_PREFIX_LIST.getTemplate(), profile, dbDatabase, dbTable);
                            log.info("sql= {} , key= {}", jdbcSql, redisKey);
                            executeSql(loopGetDynamicDsInfo(dbDatabase), jdbcSql, redisKey, redisMapping);
                        }
                        if (ArrayUtils.contains(ruleArr, MaxwellBinlogConstants.REDIS_RULE_3)) {
                        }
                    }
                }
            }
        });
        return false;
    }

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

    private void executeSql(DynamicDsInfo dynamicDsInfo, String sql, String redisKey, RedisMapping redisMapping) {

        try {
            Object object = springContextUtils.getBean(dynamicDsInfo.getBizBeanName());
            Class cls = object.getClass();
            Method method = cls.getMethod(DynGenerateClassUtils.BIZ_DEFAULT_METHOD_NAME, new Class[]{String.class});
            List<Map<String, Object>> dbDataList = (List<Map<String, Object>>) method.invoke(object, sql);
            log.info("数据库源数据返回,dynamicDsInfo={}, sql={},list={}", dynamicDsInfo, sql, dbDataList);

            if (null == dbDataList || dbDataList.isEmpty()) {
                //处理DB查询无数据情况
                CacheListDTO cacheListDTO = new CacheListDTO().setNone(true);
                cacheListDTO.setObj(Lists.newArrayList());
                setValueToRedis(redisMapping, redisKey, cacheListDTO);
            } else {
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
                setValueToRedis(redisMapping, redisKey, cacheListDTO);
            }


        } catch (Exception e) {
            log.error("反射执行BIZ出错,e=", e);
        }
    }

    private void setValueToRedis(RedisMapping redisMapping, String redisKey, CacheListDTO cacheListDTO) {
        try {
            if (redisMapping.getExpire().equals(-1)) {
                stringRedisTemplateHelper.set(redisKey, cacheListDTO);
            } else {
                stringRedisTemplateHelper.set(redisKey, cacheListDTO, redisMapping.getExpire(), TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            log.error("redis存值出错, e={}", e);
        }
    }

}
