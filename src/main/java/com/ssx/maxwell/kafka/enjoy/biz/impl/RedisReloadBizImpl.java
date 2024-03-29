package com.ssx.maxwell.kafka.enjoy.biz.impl;

import com.google.common.base.Strings;
import com.ssx.maxwell.kafka.enjoy.biz.RedisReloadBiz;
import com.ssx.maxwell.kafka.enjoy.common.helper.RedisCacheListDTOHelper;
import com.ssx.maxwell.kafka.enjoy.common.helper.RedissonHelper;
import com.ssx.maxwell.kafka.enjoy.common.model.bo.RedisMappingBO;
import com.ssx.maxwell.kafka.enjoy.common.model.datao.RedisMappingDO;
import com.ssx.maxwell.kafka.enjoy.enumerate.MaxwellBinlogConstants;
import com.ssx.maxwell.kafka.enjoy.service.RedisMappingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/12 13:47
 * @description: 重新装载缓存至redis
 */
@Service
@Slf4j
public class RedisReloadBizImpl implements RedisReloadBiz {
    @Autowired
    private RedissonHelper redissonHelper;
    @Autowired
    private RedisMappingService redisMappingService;
    @Autowired
    private RedisCacheListDTOHelper redisCacheListDTOHelper;

    @Override
    public boolean reloadCache(@NonNull String dbDatabase, @NonNull String dbTable, @NonNull String dbPid, @NonNull Map dataJson, @Nullable  Map oldDataJson) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(dbDatabase).append(dbTable);
        try {
            redissonHelper.lock(stringBuilder.toString(), 10, 5, TimeUnit.SECONDS, () -> {
                RedisMappingBO redisMappingBO = new RedisMappingBO();
                redisMappingBO.setDbDatabase(dbDatabase).setDbTable(dbTable);
                RedisMappingDO redisMapping = redisMappingService.getByDatabaseAndTable(redisMappingBO);
                if (null != redisMapping) {
                    String rule = redisMapping.getRule();
                    if (!Strings.isNullOrEmpty(rule) && !MaxwellBinlogConstants.REDIS_RULE_0.equals(rule)) {
                        String[] ruleArr = rule.split(",");
                        if (ArrayUtils.isNotEmpty(ruleArr)) {
                            if (ArrayUtils.contains(ruleArr, MaxwellBinlogConstants.REDIS_RULE_1)) {
                                redisCacheListDTOHelper.primaryRedisCacheLoadAndGet(redisMapping, dbDatabase, dbTable, dbPid);
                            }
                            if (ArrayUtils.contains(ruleArr, MaxwellBinlogConstants.REDIS_RULE_2)) {
                                redisCacheListDTOHelper.allTableRedisCacheLoadAndGet(redisMapping, dbDatabase, dbTable);
                            }
                            if (ArrayUtils.contains(ruleArr, MaxwellBinlogConstants.REDIS_RULE_3)) {
                                redisCacheListDTOHelper.customRedisCacheLoad(redisMapping, dbDatabase, dbTable, dataJson);
                            }
                        }
                    }
                }
            });
            return true;
        } catch (UnsupportedEncodingException e) {
            log.error("重新装载缓存至redis,URL编码异常e={}", e);
            return false;
        }
    }
}
