package com.ssx.maxwell.kafka.enjoy.consumer.redis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ssx.maxwell.kafka.enjoy.biz.impl.RedisReloadBizImpl;
import com.ssx.maxwell.kafka.enjoy.common.helper.BeanHelper;
import com.ssx.maxwell.kafka.enjoy.common.helper.KafkaHelper;
import com.ssx.maxwell.kafka.enjoy.common.helper.StringRedisTemplateHelper;
import com.ssx.maxwell.kafka.enjoy.common.model.bo.RedisMappingBO;
import com.ssx.maxwell.kafka.enjoy.common.model.db.RedisMappingDO;
import com.ssx.maxwell.kafka.enjoy.common.model.dto.RedisExpireAndLoadDTO;
import com.ssx.maxwell.kafka.enjoy.common.tools.JsonUtils;
import com.ssx.maxwell.kafka.enjoy.common.tools.PatternUtils;
import com.ssx.maxwell.kafka.enjoy.common.tools.UnicodeUtils;
import com.ssx.maxwell.kafka.enjoy.enumerate.MaxwellBinlogConstants;
import com.ssx.maxwell.kafka.enjoy.service.RedisMappingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author: shuaishuai.xiao
 * @date: 2019-6-5 15:17:00
 * @description: redis失效缓存
 */
@Component
@Slf4j
@ConditionalOnProperty(prefix = "maxwell.enjoy.redis", name = "expire-kafka-consumer", havingValue = "true")
public class RedisExpireConsumer {

    private static final String logPrefix = "maxwell--<redis失效缓存>--消费消息-->";
    @Autowired
    private StringRedisTemplateHelper stringRedisTemplateHelper;
    @Autowired
    private RedisMappingService redisMappingService;
    @Autowired
    private KafkaHelper kafkaHelper;
    @Value("${maxwell.enjoy.redis.reload-topic}")
    private String loadRedisTopic;
    @Autowired
    private BeanHelper beanHelper;
    @Value("${spring.profiles.active:dev}")
    private String profile;

    @KafkaListener(topics = "${maxwell.enjoy.redis.expire-topic}", groupId = "${maxwell.enjoy.redis.expire-topic-kafka-group}", containerFactory = "manualListenerContainerFactory")
    public void receive(List<ConsumerRecord<String, String>> integerStringConsumerRecords, Acknowledgment acknowledgment) {
        log.info(logPrefix + ", integerStringConsumerRecords={}", integerStringConsumerRecords);
        try {
            for (ConsumerRecord<String, String> consumerRecord : integerStringConsumerRecords) {
                String value = consumerRecord.value();
                log.info(logPrefix + "value={}", value);
                RedisExpireAndLoadDTO redisExpireDTO =
                        JsonUtils.getMapper().readValue(value, new TypeReference<RedisExpireAndLoadDTO>() {
                        });
                log.info(logPrefix + "redisExpireDTO={}", redisExpireDTO);
                List<String> keyList = Lists.newArrayList();
                keyList.addAll(redisExpireDTO.getKeyList());
                log.info("redisExpireDTO.getKeyList()={}", redisExpireDTO.getKeyList());
                try {
                    Map oldDataJson = redisExpireDTO.getOldDataJson();
                    if (null != oldDataJson && !oldDataJson.isEmpty()) {
                        RedisMappingBO redisMappingBO = new RedisMappingBO();
                        BeanUtils.copyProperties(redisExpireDTO, redisMappingBO);
                        RedisMappingDO redisMapping = redisMappingService.getByDatabaseAndTable(redisMappingBO);
                        if (!Strings.isNullOrEmpty(redisMapping.getRule()) && redisMapping.getRule().contains(MaxwellBinlogConstants.REDIS_RULE_3)
                                && !Strings.isNullOrEmpty(redisMapping.getTemplate())) {
                            String templates = redisMapping.getTemplate();
                            String[] templateArray = templates.split(",");
                            if (null != templateArray && templateArray.length > 0) {
                                for (String temp : templateArray) {
                                    if (!Strings.isNullOrEmpty(columnFilter(temp, oldDataJson))) {
                                        assemblyRedisKey(redisMapping, temp, oldDataJson, redisExpireDTO.getDbPid(), keyList);
                                    }
                                }
                            }
                        }
                    }
                    log.info("keyList={}", keyList);
                    stringRedisTemplateHelper.delete(keyList);
                } catch (Exception redisException) {
                    //todo 2019-6-14 13:47:47 如果出现删除失败、redis连接等情况、将数据落入DB、由定时任务扫表进行清除动作、当然扫表有可能也失败同时增加预警
                    //总之为了尽快清除redis中的脏数据，存在的越久数据不一致也就越久
                    log.error(logPrefix + "清除redis key异常redisExpireDTO={}, e={}", redisExpireDTO, redisExpireDTO);
                    throw redisException;
                }
                //首先清除缓存、避免db和redis数据不一致、当然清除也会出现不一致、mq消费的越晚延迟越高、
                //通知主动加载缓存
                //主动加载缓存不订阅清除缓存的队列的reason是因为
                //如果使用当前队列需要有前后依赖，当清除以后在能触发reload，如果reload在delete就会有问题
                kafkaHelper.sendMQ(loadRedisTopic, value
                        , result -> log.info(logPrefix + "reload缓存发送MQ成功, result={}", result)
                        , ex -> {
                            //在查询处去做兼容缓存没有去查库
                            log.error(logPrefix + "reload缓存发送MQ失败, 消息 value={}, ex={}", value, ex);
                        }
                );
            }
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error(logPrefix + "消费异常, e={}", e);
        }
    }

    /**
     * 过滤该模板有无存在修改的字段、如果存在修改的字段将该模板返回、如果不包含返回空串
     *
     * @param template
     * @param oldDataJson
     * @return
     */
    private String columnFilter(String template, Map oldDataJson) {
        Set<String> keySet = oldDataJson.keySet();
        for (String column : keySet) {
            if (template.contains(column)) {
                return template;
            }
        }
        return "";
    }
    /**
     * 功能描述: 组装redis 旧的自定义key
     * @param: [redisMapping, template, oldDataJson, dbPid, keyList]
     * @return: void
     * @author: shuaishuai.xiao
     * @date: 2019/6/28 18:14
     */
    private void assemblyRedisKey(RedisMappingDO redisMapping, String template, Map oldDataJson, String dbPid, List<String> keyList) {
        //:order_code:is_deleted(1800)
        //:goods_name:is_deleted(3600)
        String[] columnArray = template.split(":");
        if (null != columnArray && columnArray.length > 0) {
            Map<String, Object> dbObjectMap = null;
            //自定义缓存
            String jdbcSql = MessageFormat.format(MaxwellBinlogConstants.RedisRunSqlTemplateEnum.SQL_PRIMARY_ID.getTemplate(), redisMapping.getDbTable(), dbPid);
            String redisKey = MessageFormat.format(MaxwellBinlogConstants.RedisCacheKeyTemplateEnum.REDIS_CACHE_KEY_TEMPLATE_PREFIX_CUSTOM.getTemplate(), profile, redisMapping.getDbDatabase(), redisMapping.getDbTable());
            log.info("sql= {} , key= {}", jdbcSql, redisKey);
            List<Map<String, Object>> dbDataList = beanHelper.queryDbList(beanHelper.loopGetDynamicDsInfo(redisMapping.getDbDatabase()), jdbcSql);
            if (null == dbDataList || dbDataList.isEmpty()) {
                log.warn("自定义缓存查询数据库集合为空, 不进行构建, redisMapping={}", redisMapping);
            } else {
                dbObjectMap = dbDataList.get(0);
            }
            StringBuilder keyBuilder = new StringBuilder();
            for (int k = 0; k < columnArray.length; k++) {
                String field = null;
                if (k == columnArray.length - 1) {
                    //：最后一位过滤掉(过期时间)这部分内容
                    field = columnArray[k].  substring(0, columnArray[k].indexOf("("));
                } else {
                    field = columnArray[k];
                }
                if (!Strings.isNullOrEmpty(field)) {
                    keyBuilder.append(":");
                    //从old json 修改的字段取值
                    if(oldDataJson.containsKey(field)){
                        if(oldDataJson.get(field) instanceof String){
                            beanHelper.appendRedisKeySuffix((String) oldDataJson.get(field), keyBuilder);
                        }else {
                            beanHelper.appendRedisKeySuffix(String.valueOf(oldDataJson.get(field)), keyBuilder);
                        }
                    }else {
                        //不存在证明该字段未做修改， 从数据库取
                        Object dbObj = dbObjectMap.get(field);
                        beanHelper.appendRedisKeySuffix(dbObj, keyBuilder);
                    }
                }
            }
            keyList.add(redisKey+ keyBuilder.toString());
        }
    }
    
}
