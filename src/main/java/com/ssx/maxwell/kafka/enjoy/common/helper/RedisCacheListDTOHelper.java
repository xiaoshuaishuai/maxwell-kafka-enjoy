package com.ssx.maxwell.kafka.enjoy.common.helper;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ssx.maxwell.kafka.enjoy.common.model.db.RedisMappingDO;
import com.ssx.maxwell.kafka.enjoy.common.model.dto.RedisCacheListDTO;
import com.ssx.maxwell.kafka.enjoy.common.model.dto.RedisExpireAndLoadDTO;
import com.ssx.maxwell.kafka.enjoy.common.tools.StringUtils;
import com.ssx.maxwell.kafka.enjoy.configuration.DynamicDsInfo;
import com.ssx.maxwell.kafka.enjoy.enumerate.MaxwellBinlogConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/7/3 16:22
 * @description:
 */
@Component
@Slf4j
public class RedisCacheListDTOHelper {
    @Autowired
    private StringRedisTemplateHelper stringRedisTemplateHelper;
    @Autowired
    private BeanHelper beanHelper;
    @Value("${spring.profiles.active:dev}")
    private String profile;

    /**
     * 单表主键缓存
     * @param redisMapping
     * @param dbDatabase
     * @param dbTable
     * @param dbPid
     * @return
     */
    public RedisCacheListDTO primaryRedisCacheLoadAndGet(RedisMappingDO redisMapping, String dbDatabase, String dbTable, String dbPid) {
        //单表主键缓存
        String jdbcSql = MessageFormat.format(MaxwellBinlogConstants.RedisRunSqlTemplateEnum.SQL_PRIMARY_ID.getTemplate(), dbTable, dbPid);
        String redisKey = MessageFormat.format(MaxwellBinlogConstants.RedisCacheKeyTemplateEnum.REDIS_CACHE_KEY_TEMPLATE_ITEM_PK_ID.getTemplate(), profile, dbDatabase, dbTable, dbPid);
        log.info("sql= {} , key= {}", jdbcSql, redisKey);
        return executeToRedis(beanHelper.loopGetDynamicDsInfo(dbDatabase), jdbcSql, redisKey, redisMapping, false, redisMapping.getPrimaryExpire());
    }

    /**
     * 全表缓存
     * @param redisMapping
     * @param dbDatabase
     * @param dbTable
     * @return
     */
    public RedisCacheListDTO allTableRedisCacheLoadAndGet(RedisMappingDO redisMapping, String dbDatabase, String dbTable) {
        //全表缓存
        String jdbcSql = MessageFormat.format(MaxwellBinlogConstants.RedisRunSqlTemplateEnum.SQL_ALL.getTemplate(), dbTable);
        String redisKey = MessageFormat.format(MaxwellBinlogConstants.RedisCacheKeyTemplateEnum.REDIS_CACHE_KEY_TEMPLATE_PREFIX_LIST.getTemplate(), profile, dbDatabase, dbTable);
        log.info("sql= {} , key= {}", jdbcSql, redisKey);
        return executeToRedis(beanHelper.loopGetDynamicDsInfo(dbDatabase), jdbcSql, redisKey, redisMapping, false, redisMapping.getTableExpire());
    }

    /**
     * 自定义缓存
     * @param redisMapping
     * @param dbDatabase
     * @param dbTable
     * @param reloadKeyDTOS
     * @return
     */
    public void customRedisCacheLoad(@NonNull RedisMappingDO redisMapping, @NonNull String dbDatabase, @NonNull String dbTable, @Nullable List<RedisExpireAndLoadDTO.ReloadKeyDTO> reloadKeyDTOS, @NonNull Map dataJson) {
        if(CollectionUtils.isEmpty(reloadKeyDTOS)){
            return;
        }
        for(RedisExpireAndLoadDTO.ReloadKeyDTO reloadKeyDTO :  reloadKeyDTOS){
            String[] fuzzyKeyArray = reloadKeyDTO.getFuzzyKey().split(":");
            String[] templatesArray = reloadKeyDTO.getTemplates().split(":");
            List<String> columnList = Lists.newArrayList();
            for (int i = 0; i < fuzzyKeyArray.length; i++) {
                if(!fuzzyKeyArray[i].contains("*")){
                    if(fuzzyKeyArray[i].contains("(")){
                        //筛选掉过期时间配置
                        columnList.add(templatesArray[i].substring(0, templatesArray[i].indexOf("(")));
                    }
                }
            }
            StringBuilder whereSql = new StringBuilder();
            columnList.forEach(column->{
                if(whereSql.toString().contains("=")){
                    whereSql.append(" AND ");
                }
                Object dbValue = dataJson.get(column);
                if(null == dbValue){
                    whereSql.append(column).append(" = ").append("");
                }else {
                    if(dbValue instanceof String){
                        whereSql.append(column).append(" = ").append("'").append(dbValue).append("'");
                    }else {
                        whereSql.append(column).append(" = ").append(dbValue);
                    }
                }

            });
            //自定义缓存
            String jdbcSql = MessageFormat.format(MaxwellBinlogConstants.RedisRunSqlTemplateEnum.SQL_CUSTOM.getTemplate(), dbTable, whereSql.toString());
            String redisKey = MessageFormat.format(MaxwellBinlogConstants.RedisCacheKeyTemplateEnum.REDIS_CACHE_KEY_TEMPLATE_PREFIX_CUSTOM.getTemplate(), profile, dbDatabase, dbTable);
            log.info("sql= {} , key= {}", jdbcSql, redisKey);
            customExecuteToRedis(beanHelper.loopGetDynamicDsInfo(dbDatabase), jdbcSql, redisKey, redisMapping, reloadKeyDTO.getTemplates(), Long.MAX_VALUE);
        }
    }

    /**
     * 自定义缓存
     * @param key
     * @param template
     * @return
     */
    public RedisCacheListDTO customRedisCacheLoadAndGet(String key, String template){
        //fixme dev:test:sys_order:custom:\\u6d77\\u98de\\u4e1d\\u6d17\\u53d1\\u6c34:0
        return null;
    }
    /**
     * 功能描述: 执行至redis业务
     *
     * @param: [dynamicDsInfo, sql, redisKey, redisMapping, isCustom, rule]
     * @return: RedisCacheListDTO
     * @author: shuaishuai.xiao
     * @date: 2019/6/14 17:31
     */
    private RedisCacheListDTO executeToRedis(DynamicDsInfo dynamicDsInfo, String sql, String redisKey, RedisMappingDO redisMapping, boolean isCustom, Long expire) {

        if (isCustom) {
            //自定义缓存
            String template = redisMapping.getTemplate();
            log.info("自定义缓存, template={}", template);
            if (!Strings.isNullOrEmpty(template)) {
                List<Map<String, Object>> dbDataList = beanHelper.queryDbList(dynamicDsInfo, sql);
                if (null == dbDataList || dbDataList.isEmpty()) {
                    log.warn("自定义缓存查询数据库集合为空, 不进行构建, redisMapping={}", redisMapping);
                } else {
                    String[] templateArr = template.split(",");
                    if (null != templateArr && templateArr.length > 0) {
                        //按照主键ID确保只有一个值返回, 如果一个主键查到多个返回值、检查应用健康情况
                        Map<String, Object> stringObjectMap = dbDataList.get(0);
                        for (String part : templateArr) {
                            //:order_code:is_deleted(3600),
                            //:goods_name:is_deleted(-1)
                            String[] a2 = part.split(":");
                            StringBuilder keyBuilder = new StringBuilder();
                            for (int k = 0; k < a2.length; k++) {
                                String field = null;
                                if (k == a2.length - 1) {
                                    //：最后一位过滤掉(过期时间)这部分内容
                                    field = a2[k].substring(0, a2[k].indexOf("("));
                                } else {
                                    field = a2[k];
                                }
                                if (!Strings.isNullOrEmpty(field)) {
                                    keyBuilder.append(":");
                                    Object dbObj = stringObjectMap.get(field);
                                    beanHelper.appendRedisKeySuffix(dbObj, keyBuilder);
                                }
                            }
                            //获取自定义缓存过期时间
                            expire = Long.valueOf(part.substring(part.indexOf("(") + 1, part.indexOf(")")));
                            String finalKey = keyBuilder.insert(0, redisKey).toString();
                            log.info("自定义缓存, key={}, expire={}", finalKey, expire);
                            RedisCacheListDTO redisCacheListDTO = buildCacheListDTO(dbDataList);
                            setValueToRedis(finalKey, redisCacheListDTO, expire);
                            return redisCacheListDTO;
                        }
                    }
                }

            }
        } else {
            List<Map<String, Object>> dbDataList = beanHelper.queryDbList(dynamicDsInfo, sql);
            if (null == dbDataList || dbDataList.isEmpty()) {
                //处理DB查询无数据情况
                RedisCacheListDTO cacheListDTO = new RedisCacheListDTO().setNone(true);
                cacheListDTO.setObj(Lists.newArrayList());
                setValueToRedis(redisKey, cacheListDTO, expire);
                return cacheListDTO;
            } else {
                RedisCacheListDTO redisCacheListDTO = buildCacheListDTO(dbDataList);
                setValueToRedis(redisKey, redisCacheListDTO, expire);
                return redisCacheListDTO;
            }
        }
        return null;
    }

    private void customExecuteToRedis(DynamicDsInfo dynamicDsInfo, String sql, String redisKey, RedisMappingDO redisMapping, String template, Long expire) {
        log.info("自定义缓存, template={}", template);
        if (!Strings.isNullOrEmpty(template)) {
            List<Map<String, Object>> dbDataList = beanHelper.queryDbList(dynamicDsInfo, sql);
            if (null == dbDataList || dbDataList.isEmpty()) {
                log.warn("自定义缓存查询数据库集合为空, 不进行构建, sql={}", sql);
            } else {
                String[] templateArr = template.split(",");
                if (null != templateArr && templateArr.length > 0) {
                    for(Map<String, Object> stringObjectMap :dbDataList){
                        for (String part : templateArr) {
                            //:order_code:is_deleted(3600),
                            //:goods_name:is_deleted(-1)
                            String[] a2 = part.split(":");
                            StringBuilder keyBuilder = new StringBuilder();
                            for (int k = 0; k < a2.length; k++) {
                                String field = null;
                                if (k == a2.length - 1) {
                                    //：最后一位过滤掉(过期时间)这部分内容
                                    field = a2[k].substring(0, a2[k].indexOf("("));
                                } else {
                                    field = a2[k];
                                }
                                if (!Strings.isNullOrEmpty(field)) {
                                    keyBuilder.append(":");
                                    Object dbObj = stringObjectMap.get(field);
                                    beanHelper.appendRedisKeySuffix(dbObj, keyBuilder);
                                }
                            }
                            //获取自定义缓存过期时间
                            expire = Long.valueOf(part.substring(part.indexOf("(") + 1, part.indexOf(")")));
                            String finalKey = keyBuilder.insert(0, redisKey).toString();
                            log.info("自定义缓存, key={}, expire={}", finalKey, expire);
                            RedisCacheListDTO redisCacheListDTO = buildCacheListDTO(dbDataList);
                            setValueToRedis(finalKey, redisCacheListDTO, expire);
                        }
                    }
                }
            }

        }
    }

    /**
     * 功能描述: set to redis
     *
     * @param: [redisMapping, redisKey, cacheListDTO, expire]
     * @return: void
     * @author: shuaishuai.xiao
     * @date: 2019/6/14 17:29
     */
    private void setValueToRedis(String redisKey, RedisCacheListDTO cacheListDTO, Long expire) {
        try {
            if ("-1".equals(expire) || -1 == expire) {
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
    private RedisCacheListDTO buildCacheListDTO(List<Map<String, Object>> dbDataList) {
        RedisCacheListDTO cacheListDTO = new RedisCacheListDTO().setNone(false);
        List<Map<String, Object>> cacheList = new ArrayList(dbDataList.size());
        for (Object dbObj : dbDataList) {
            Map<String, Object> map = (Map<String, Object>) dbObj;
            Map<String, Object> cacheMap = Maps.newHashMap();
            Set<String> fieldSet = map.keySet();
            for (String field : fieldSet) {
                cacheMap.put(StringUtils.lineToHump(field), map.get(field));
            }
            cacheList.add(cacheMap);
        }
        cacheListDTO.setObj(cacheList);
        return cacheListDTO;
    }
}
