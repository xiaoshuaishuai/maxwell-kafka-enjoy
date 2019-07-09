package com.ssx.maxwell.kafka.enjoy.common.helper;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ssx.maxwell.kafka.enjoy.common.exception.RedisCacheListDTOException;
import com.ssx.maxwell.kafka.enjoy.common.model.datao.RedisMappingDO;
import com.ssx.maxwell.kafka.enjoy.common.model.dto.RedisCacheListDTO;
import com.ssx.maxwell.kafka.enjoy.common.tools.StringUtils;
import com.ssx.maxwell.kafka.enjoy.common.tools.TemplateUtils;
import com.ssx.maxwell.kafka.enjoy.common.tools.UrlCodeUtils;
import com.ssx.maxwell.kafka.enjoy.configuration.DynamicDsInfo;
import com.ssx.maxwell.kafka.enjoy.enumerate.MaxwellBinlogConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.UnsupportedEncodingException;
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
     *
     * @param redisMapping
     * @param dbDatabase
     * @param dbTable
     * @param dbPid
     * @return
     */
    public String primaryRedisCacheLoadAndGet(@NonNull RedisMappingDO redisMapping, @NonNull String dbDatabase, @NonNull String dbTable, @NonNull String dbPid) {
        //单表主键缓存
        String jdbcSql = MessageFormat.format(MaxwellBinlogConstants.RedisRunSqlTemplateEnum.SQL_PRIMARY_ID.getTemplate(), dbTable, "'" + dbPid + "'");
        String redisKey = MessageFormat.format(MaxwellBinlogConstants.RedisCacheKeyTemplateEnum.REDIS_CACHE_KEY_TEMPLATE_ITEM_PK_ID.getTemplate(), profile, dbDatabase, dbTable, dbPid);
        log.info("sql= {} , key= {}", jdbcSql, redisKey);
        return executePrimaryAndAllTableToRedis(beanHelper.loopGetDynamicDsInfo(dbDatabase), jdbcSql, redisKey, redisMapping.getPrimaryExpire());
    }

    /**
     * 全表缓存
     *
     * @param redisMapping
     * @param dbDatabase
     * @param dbTable
     * @return
     */
    public String allTableRedisCacheLoadAndGet(@NonNull RedisMappingDO redisMapping, @NonNull String dbDatabase, @NonNull String dbTable) {
        //全表缓存
        String jdbcSql = MessageFormat.format(MaxwellBinlogConstants.RedisRunSqlTemplateEnum.SQL_ALL.getTemplate(), dbTable, Strings.isNullOrEmpty(redisMapping.getTableOrderBy()) ? "" : redisMapping.getTableOrderBy());
        String redisKey = MessageFormat.format(MaxwellBinlogConstants.RedisCacheKeyTemplateEnum.REDIS_CACHE_KEY_TEMPLATE_PREFIX_LIST.getTemplate(), profile, dbDatabase, dbTable);
        log.info("sql= {} , key= {}", jdbcSql, redisKey);
        return executePrimaryAndAllTableToRedis(beanHelper.loopGetDynamicDsInfo(dbDatabase), jdbcSql, redisKey, redisMapping.getTableExpire());
    }

    /**
     * 自定义缓存
     *
     * @param redisMapping
     * @param dbDatabase
     * @param dbTable
     * @return
     */
    public void customRedisCacheLoad(@NonNull RedisMappingDO redisMapping, @NonNull String dbDatabase, @NonNull String dbTable, @NonNull Map dataJson) throws UnsupportedEncodingException {
        if (!Strings.isNullOrEmpty(redisMapping.getTemplate())) {
            String[] templateArr = redisMapping.getTemplate().split(",");
            if (ArrayUtils.isNotEmpty(templateArr)) {
                String[] templateOrderBy = new String[templateArr.length];
                for (int i = 0; i < templateArr.length; i++) {
                    templateOrderBy[i] = "";
                }
                if(!Strings.isNullOrEmpty(redisMapping.getTemplateOrderBy())){
                    String[] templateOrderByArray = redisMapping.getTemplateOrderBy().split(",");
                    if(null != templateOrderByArray && templateArr.length == templateOrderByArray.length){
                        for (int i = 0; i < templateOrderByArray.length; i++) {
                            templateOrderBy[i] = templateOrderByArray[i];
                        }
                    }
                }
                String redisKey = MessageFormat.format(MaxwellBinlogConstants.RedisCacheKeyTemplateEnum.REDIS_CACHE_KEY_TEMPLATE_PREFIX_CUSTOM.getTemplate(), profile, dbDatabase, dbTable);
                for (int i = 0; i < templateArr.length; i++) {
                    String keySuffix = TemplateUtils.templateConversionKeyAlone(templateArr[i], dataJson);
                    String conversionKey = redisKey + keySuffix;
                    log.info("处理自定义缓存redisKey={}", conversionKey);
                    this.customRedisCacheLoadAndGet(conversionKey, templateArr[i], templateOrderBy[i]);
                }
            }
        }
        return;
        /*  模糊key删除=---------------------------------------------------------
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
        }*/
    }

    /**
     * 自定义缓存
     *
     * @param key
     * @param template
     * @param template
     * @return
     */
    public String customRedisCacheLoadAndGet(String key, String template, @Nullable String orderBy) throws UnsupportedEncodingException {
        // dev:test:sys_order:3:custom:code6:0
        //:goods_name:is_deleted(3600)
        String[] keyArray = key.split(":");
        String[] envPrefix = new String[5];
        StringBuilder sbAfter = new StringBuilder();
        int idx = 0;
        for (int i = 0; i < keyArray.length; i++) {
            if (i > 4) {
                sbAfter.append(":").append(keyArray[i] + "");
            } else {
                envPrefix[idx] = keyArray[i];
                idx++;
            }
        }
        String keyAfterStr = sbAfter.toString();
        if (Strings.isNullOrEmpty(keyAfterStr)) {
            throw new RedisCacheListDTOException("客户端传入的key错误, 按照模板截取key之后为空");
        }
        //:\\u6d77\\u98de\\u4e1d\\u6d17\\u53d1\\u6c34:0"
        String[] keyAfterArray = keyAfterStr.split(":");
        //:order_code:is_deleted(1800)
        String[] templateArray = template.split(":");
        if (keyAfterArray.length != templateArray.length) {
            throw new RedisCacheListDTOException("客户端传入的key和模板值对应不上, 原始key=" + key + ", key截取后=" + keyAfterStr + ", 模板=" + template);
        }
        StringBuilder whereSql = new StringBuilder();
        for (int i = 0; i < templateArray.length; i++) {
            String column = templateArray[i];
            if (Strings.isNullOrEmpty(column)) {
                continue;
            }
            if (whereSql.toString().contains("=")) {
                whereSql.append(" AND ");
            }

            if (column.contains("(")) {
                //筛选掉过期时间配置
                column = column.substring(0, column.indexOf("("));
            }
            String dbValue = keyAfterArray[i];
            if (Strings.isNullOrEmpty(dbValue)) {
                whereSql.append(column).append(" IS NULL ");
            } else {
                if (UrlCodeUtils.isEncode(dbValue)) {
                    dbValue = UrlCodeUtils.decode(dbValue);
                }
                whereSql.append(column).append(" = ").append("'").append(dbValue).append("'");
            }
        }
        //获取自定义缓存过期时间
        Long expire = Long.valueOf(template.substring(template.indexOf("(") + 1, template.indexOf(")")));
        //自定义缓存
        String jdbcSql = MessageFormat.format(MaxwellBinlogConstants.RedisRunSqlTemplateEnum.SQL_CUSTOM.getTemplate(), envPrefix[2], whereSql.toString(), Strings.isNullOrEmpty(orderBy) ? "" : orderBy);
        String redisKey = MessageFormat.format(MaxwellBinlogConstants.RedisCacheKeyTemplateEnum.REDIS_CACHE_KEY_TEMPLATE_PREFIX_CUSTOM.getTemplate(), envPrefix[0], envPrefix[1], envPrefix[2]);
        String finalKey = redisKey + keyAfterStr;
        log.info("sql= {} , key= {}", jdbcSql, finalKey);
        List<Map<String, Object>> dbDataList = beanHelper.queryDbList(beanHelper.loopGetDynamicDsInfo(envPrefix[1]), jdbcSql);
        if (null == dbDataList || dbDataList.isEmpty()) {
            log.warn("自定义缓存查询数据库集合为空, 不进行构建, sql={}", jdbcSql);
        } else {
            RedisCacheListDTO redisCacheListDTO = buildCacheListDTO(dbDataList);
            return setValueToRedis(finalKey, redisCacheListDTO, expire);
        }
        return null;
    }

    /**
     * 功能描述: 执行至redis业务
     *
     * @param: [dynamicDsInfo, sql, redisKey, redisMapping, isCustom, rule]
     * @return: String
     * @author: shuaishuai.xiao
     * @date: 2019/6/14 17:31
     */
    private String executePrimaryAndAllTableToRedis(DynamicDsInfo dynamicDsInfo, String sql, String redisKey, Long expire) {
        //主键缓存 全表缓存走这里
        List<Map<String, Object>> dbDataList = beanHelper.queryDbList(dynamicDsInfo, sql);
        if (null == dbDataList || dbDataList.isEmpty()) {
            //处理DB查询无数据情况
            RedisCacheListDTO cacheListDTO = new RedisCacheListDTO().setNone(true);
            cacheListDTO.setObj(Lists.newArrayList());
            return setValueToRedis(redisKey, cacheListDTO, expire);
        } else {
            RedisCacheListDTO redisCacheListDTO = buildCacheListDTO(dbDataList);
            return setValueToRedis(redisKey, redisCacheListDTO, expire);
        }
    }

    private void customExecuteToRedis(DynamicDsInfo dynamicDsInfo, String sql, String redisKeyPrefix, String template) throws UnsupportedEncodingException {
        log.info("自定义缓存, template={}", template);
        if (!Strings.isNullOrEmpty(template)) {
            List<Map<String, Object>> dbDataList = beanHelper.queryDbList(dynamicDsInfo, sql);
            if (null == dbDataList || dbDataList.isEmpty()) {
                log.warn("自定义缓存查询数据库集合为空, 不进行构建, sql={}", sql);
                return;
            } else {
                buildRedisCacheListDTO(template, dbDataList, redisKeyPrefix);
            }

        }
    }

    private void buildRedisCacheListDTO(String template, List<Map<String, Object>> dbDataList, String redisKeyPrefix) throws UnsupportedEncodingException {
        String[] templateArray = template.split(":");
        Map<String, List<Map<String, Object>>> keySuffixMap = Maps.newHashMap();
        for (Map<String, Object> dbObjMap : dbDataList) {
            StringBuilder keyBuilderSuffix = new StringBuilder();
            for (int k = 0; k < templateArray.length; k++) {
                String column = null;
                if (k == templateArray.length - 1) {
                    //：最后一位过滤掉(过期时间)这部分内容
                    column = templateArray[k].substring(0, templateArray[k].indexOf("("));
                } else {
                    column = templateArray[k];
                }
                if (!Strings.isNullOrEmpty(column)) {
                    keyBuilderSuffix.append(":");
                    Object dbObj = dbObjMap.get(column);
                    TemplateUtils.encodeRedisKeySuffix(dbObj, keyBuilderSuffix);
                }
            }
            String keySuffix = keyBuilderSuffix.toString();
            Map<String, Object> cacheMap = Maps.newHashMap();
            Set<String> fieldSet = dbObjMap.keySet();
            for (String field : fieldSet) {
                cacheMap.put(StringUtils.lineToHump(field), dbObjMap.get(field));
            }
            if (keySuffixMap.containsKey(keySuffix)) {
                keySuffixMap.get(keySuffix).add(cacheMap);
            } else {
                List<Map<String, Object>> mapList = Lists.newArrayList();
                mapList.add(cacheMap);
                keySuffixMap.put(keySuffix, mapList);
            }
        }
        //获取自定义缓存过期时间
        Long expire = Long.valueOf(template.substring(template.indexOf("(") + 1, template.indexOf(")")));
        Set<String> keySuffixSet = keySuffixMap.keySet();
        for (String keySuffix : keySuffixSet) {
            String finalKey = new String(redisKeyPrefix + keySuffix);
            log.info("自定义缓存, key={}, expire={}", finalKey, expire);
            RedisCacheListDTO cacheListDTO = new RedisCacheListDTO();
            cacheListDTO.setNone(CollectionUtils.isEmpty(keySuffixMap.get(keySuffix)) ? true : false);
            cacheListDTO.setObj(CollectionUtils.isEmpty(keySuffixMap.get(keySuffix)) ? Lists.newArrayList() : keySuffixMap.get(keySuffix));
            setValueToRedis(finalKey, cacheListDTO, expire);
        }
    }

    /**
     * 功能描述: set to redis
     *
     * @param: [redisMapping, redisKey, cacheListDTO, expire]
     * @return: String
     * @author: shuaishuai.xiao
     * @date: 2019/6/14 17:29
     */
    public String setValueToRedis(String redisKey, RedisCacheListDTO cacheListDTO, Long expire) {
        try {
            if ("-1".equals(expire) || -1 == expire) {
                return stringRedisTemplateHelper.set(redisKey, cacheListDTO);
            } else {
                return stringRedisTemplateHelper.set(redisKey, cacheListDTO, expire, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            log.error("redis存值出错, e={}", e);
        }
        return null;
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
