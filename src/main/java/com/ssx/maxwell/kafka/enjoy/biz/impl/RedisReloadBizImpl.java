package com.ssx.maxwell.kafka.enjoy.biz.impl;

import com.google.common.base.Strings;
import com.ssx.maxwell.kafka.enjoy.biz.RedisReloadBiz;
import com.ssx.maxwell.kafka.enjoy.common.helper.DistributedLock;
import com.ssx.maxwell.kafka.enjoy.common.helper.RedissonHelper;
import com.ssx.maxwell.kafka.enjoy.common.model.bo.RedisMappingBO;
import com.ssx.maxwell.kafka.enjoy.common.model.entity.RedisMapping;
import com.ssx.maxwell.kafka.enjoy.common.tools.PatternUtils;
import com.ssx.maxwell.kafka.enjoy.common.tools.UnicodeUtils;
import com.ssx.maxwell.kafka.enjoy.enumerate.MaxwellBinlogConstants;
import com.ssx.maxwell.kafka.enjoy.service.RedisMappingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Map;
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
                            String jdbcSql = MessageFormat.format(MaxwellBinlogConstants.RedisRunSqlTemplateEnum.SQL_PRIMARY_ID.getTemplate(), dbTable, dbPid);
                            String redisKey = MessageFormat.format(MaxwellBinlogConstants.RedisCacheKeyTemplateEnum.REDIS_CACHE_KEY_TEMPLATE_ITEM_PKID.getTemplate(), profile, dbDatabase, dbTable, dbPid);
                            log.info("sql= {} , key= {}",jdbcSql, redisKey);
                            //todo 2019-6-12 17:47:49 动态执行sql语句并将结果集放入缓存
                        }
                        if (ArrayUtils.contains(ruleArr, MaxwellBinlogConstants.REDIS_RULE_2)) {
                            String jdbcSql = MessageFormat.format(MaxwellBinlogConstants.RedisRunSqlTemplateEnum.SQL_ALL.getTemplate(), dbTable);
                            String redisKey = MessageFormat.format(MaxwellBinlogConstants.RedisCacheKeyTemplateEnum.REDIS_CACHE_KEY_TEMPLATE_PREFIX_LIST.getTemplate(), profile, dbDatabase, dbTable);
                            log.info("sql= {} , key= {}",jdbcSql, redisKey);
                        }
                        if (ArrayUtils.contains(ruleArr, MaxwellBinlogConstants.REDIS_RULE_3)) {
                        }
                    }
                }
            }
        });
        return false;
    }
}
