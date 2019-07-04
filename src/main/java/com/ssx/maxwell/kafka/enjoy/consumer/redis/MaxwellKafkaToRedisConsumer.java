package com.ssx.maxwell.kafka.enjoy.consumer.redis;

import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.ssx.maxwell.kafka.enjoy.common.helper.KafkaHelper;
import com.ssx.maxwell.kafka.enjoy.common.model.db.RedisMappingDO;
import com.ssx.maxwell.kafka.enjoy.common.model.dto.RedisExpireAndLoadDTO;
import com.ssx.maxwell.kafka.enjoy.common.tools.JsonUtils;
import com.ssx.maxwell.kafka.enjoy.common.tools.PatternUtils;
import com.ssx.maxwell.kafka.enjoy.common.tools.UnicodeUtils;
import com.ssx.maxwell.kafka.enjoy.configuration.JvmCache;
import com.ssx.maxwell.kafka.enjoy.enumerate.MaxwellBinlogConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/3 14:07
 * @description: kafka transport redis  缓存过期
 */
@Component
@Slf4j
@ConditionalOnProperty(prefix = "maxwell.enjoy.redis", name = "kafka-consumer", havingValue = "true")
public class MaxwellKafkaToRedisConsumer {

    private static final String logPrefix = "maxwell--<redis>--消费消息-->";

    @Value("${spring.profiles.active:dev}")
    private String profile;
    @Value("${maxwell.enjoy.redis.jvmCache}")
    private boolean redisMappingCacheSwitch;
    @Autowired
    private Cache<String, RedisMappingDO> redisMappingCache;

    @Value("${maxwell.enjoy.redis.expire-topic}")
    private String expireRedisTopic;

    @Autowired
    private KafkaHelper kafkaHelper;

    @KafkaListener(topics = "${maxwell.enjoy.kafka-binlog-topic}", groupId = "${maxwell.enjoy.redis.kafka-group}", containerFactory = "manualListenerContainerFactory")
    public void receive(List<ConsumerRecord<String, String>> integerStringConsumerRecords, Acknowledgment acknowledgment) {
        //maxwell--消费消息:{"database":"test","table":"title","type":"update","ts":1559542729,"xid":2470,"commit":true,"data":{"id":2,"name":"2","content":"88"},"old":{"content":"2"}}
        //maxwell--消费消息:{"database":"test","table":"title","type":"update","ts":1559542747,"xid":2498,"commit":true,"data":{"id":2,"name":"2","content":"99"},"old":{"content":"88"}}
        //maxwell--消费消息:{"database":"test","table":"title","type":"insert","ts":1559542757,"xid":2515,"commit":true,"data":{"id":3,"name":"3","content":"199"}}
//         maxwell--消费消息:{"database":"test","table":"title","type":"bootstrap-start","ts":1559548626,"data":{}}
//         maxwell--消费消息:{"database":"test","table":"title","type":"bootstrap-insert","ts":1559548626,"data":{"id":1,"name":"1","content":"3"}}
//         maxwell--消费消息:{"database":"test","table":"title","type":"bootstrap-insert","ts":1559548626,"data":{"id":2,"name":"2","content":"3"}}
//         maxwell--消费消息:{"database":"test","table":"title","type":"bootstrap-insert","ts":1559548626,"data":{"id":3,"name":"3","content":"3"}}
//         maxwell--消费消息:{"database":"test","table":"title","type":"bootstrap-complete","ts":1559548626,"data":{}}
//{"database":"test","table":"sys_order","type":"update","ts":1559640375,"xid":2012,"commit":true,"data":{"id":1,"order_code":"1","category":0,"goods_name":"牙膏","is_send_express":1,"is_deleted":0,"gmt_create":"2019-06-04 17:21:45","gmt_modify":"2019-06-04 17:26:15"},"old":{"is_send_express":0,"gmt_modify":"2019-06-04 17:25:25"}}
        log.info(logPrefix + ", integerStringConsumerRecords={}", integerStringConsumerRecords);
        try {
            for (ConsumerRecord consumerRecord : integerStringConsumerRecords) {
                log.info(logPrefix + ", consumerRecord={}", consumerRecord);
                String message = (String) consumerRecord.value();
                Map<String, Object> jsonMessageMap;
                try {
                    jsonMessageMap = JsonUtils.JsonStringToMap(message);
                } catch (IOException e) {
                    throw e;
                }
                if (null != jsonMessageMap && !jsonMessageMap.isEmpty() && jsonMessageMap.containsKey("database") && jsonMessageMap.containsKey("table")) {
                    if (redisMappingCacheSwitch) {
                        String database = (String) jsonMessageMap.get("database");
                        String table = (String) jsonMessageMap.get("table");
                        String jvmCacheKey = JvmCache.redisJvmCacheKey(profile, database, table);
                        RedisMappingDO redisMapping = redisMappingCache.getIfPresent(jvmCacheKey);
                        if (null != redisMapping) {
                            String rule = redisMapping.getRule();
                            if (MaxwellBinlogConstants.REDIS_RULE_0.equals(redisMapping.getRule())) {
                                return;
                            }
                            RedisExpireAndLoadDTO redisExpireDTO = new RedisExpireAndLoadDTO();
                            redisExpireDTO.setDbDatabase(database);
                            redisExpireDTO.setDbTable(table);
                            String type = (String) jsonMessageMap.get("type");
                            if (!Strings.isNullOrEmpty(type)) {
                                MaxwellBinlogConstants.MaxwellBinlogEnum maxwellBinlogEnum = MaxwellBinlogConstants.MaxwellBinlogEnum.getMaxwellBinlogEnum(type);
                                if (null == maxwellBinlogEnum) {
                                    log.error(logPrefix + "未识别的type, message={}", message);
                                    return;
                                }
                                if (MaxwellBinlogConstants.REDIS_CLEAR_WAITING_CACHE.equals(maxwellBinlogEnum.getOperate())) {
                                    log.info(logPrefix + "监听到bootstrap操作, message={}", message);
                                    return;
                                }
                                if (MaxwellBinlogConstants.REDIS_CLEAR_TABLE_ALL_AND_ROW_CACHE.equals(maxwellBinlogEnum.getOperate())) {
                                    log.info(logPrefix + "监听到清除REDIS全表缓存&单条缓存&自定义缓存(如果有)");
                                    if (!Strings.isNullOrEmpty(rule) && !MaxwellBinlogConstants.REDIS_RULE_0.equals(rule)) {
                                        String[] ruleArr = rule.split(",");
                                        if (ArrayUtils.isNotEmpty(ruleArr)) {
                                            Map dataJson = (Map) jsonMessageMap.get("data");
                                            Map oldDataJson = (Map) jsonMessageMap.get("old");
                                            Integer id = (Integer) dataJson.get("id");
                                            redisExpireDTO.setDbPid(String.valueOf(id));
//                                            redisExpireDTO.setDataJson(dataJson);
                                            if (ArrayUtils.contains(ruleArr, MaxwellBinlogConstants.REDIS_RULE_1)) {
                                                //处理单表主键缓存
                                                String redisKey = MessageFormat.format(MaxwellBinlogConstants.RedisCacheKeyTemplateEnum.REDIS_CACHE_KEY_TEMPLATE_ITEM_PK_ID.getTemplate(), profile, database, table, id);
                                                // id字段 修改
                                                if (null != oldDataJson && !oldDataJson.isEmpty() && oldDataJson.containsKey("id") && null != oldDataJson.get("id")) {
                                                    String key = MessageFormat.format(MaxwellBinlogConstants.RedisCacheKeyTemplateEnum.REDIS_CACHE_KEY_TEMPLATE_ITEM_PK_ID.getTemplate(), profile, database, table, oldDataJson.get("id") + "");
                                                    redisExpireDTO.getKeyList().add(key);
                                                    log.info(logPrefix + "处理单表主键缓存[id变更]redisKey={}", key);
                                                }
                                                redisExpireDTO.getKeyList().add(redisKey);
                                                log.info(logPrefix + "处理单表主键缓存redisKey={}", redisKey);
                                            }
                                            if (ArrayUtils.contains(ruleArr, MaxwellBinlogConstants.REDIS_RULE_2)) {
                                                //处理全表缓存
                                                String redisKey = MessageFormat.format(MaxwellBinlogConstants.RedisCacheKeyTemplateEnum.REDIS_CACHE_KEY_TEMPLATE_PREFIX_LIST.getTemplate(), profile, database, table);
                                                redisExpireDTO.getKeyList().add(redisKey);
                                                log.info(logPrefix + "处理全表缓存redisKey={}", redisKey);

                                            }
                                            if (ArrayUtils.contains(ruleArr, MaxwellBinlogConstants.REDIS_RULE_3)) {
                                                redisExpireDTO.setOldDataJson(oldDataJson);
                                                String template = redisMapping.getTemplate();
                                                if (!Strings.isNullOrEmpty(template)) {
                                                    String[] templateArr = template.split(",");
                                                    String redisKey = MessageFormat.format(MaxwellBinlogConstants.RedisCacheKeyTemplateEnum.REDIS_CACHE_KEY_TEMPLATE_PREFIX_CUSTOM.getTemplate(), profile, database, table);
                                                    if (ArrayUtils.isNotEmpty(templateArr)) {
                                                        for (String templateString : templateArr) {
                                                            StringBuilder columnStringBuilder = new StringBuilder();
                                                            if (!Strings.isNullOrEmpty(templateString) && templateString.contains(":")) {
                                                                String[] columnArr = templateString.split(":");
                                                                if (ArrayUtils.isNotEmpty(columnArr)) {
                                                                    for (String columnString : columnArr) {
                                                                        if (dataJson.containsKey(columnString)) {
                                                                            columnStringBuilder.append(":");
                                                                            if (dataJson.get(columnString) instanceof String) {
                                                                                //字符串判断是否包含中文
                                                                                if (PatternUtils.isContainChinese((String) dataJson.get(columnString))) {
                                                                                    //转码
                                                                                    columnStringBuilder.append(UnicodeUtils.cnToUnicode((String) dataJson.get(columnString)));
                                                                                } else {
                                                                                    //字符串类型参数为空NONE填充
                                                                                    columnStringBuilder.append(Strings.isNullOrEmpty(String.valueOf(dataJson.get(columnString))) ? MaxwellBinlogConstants.REDIS_VAL_NONE_MAGIC : dataJson.get(columnString));
                                                                                }
                                                                            } else {
                                                                                columnStringBuilder.append(null == dataJson.get(columnString) ? MaxwellBinlogConstants.REDIS_VAL_NONE_MAGIC : dataJson.get(columnString));
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
//                                                         :order_code:is_deleted
//                                                         :goods_name:is_deleted
                                                            String conversionKey = columnStringBuilder.insert(0, redisKey).toString();
                                                            log.info(logPrefix + "处理自定义缓存redisKey={}", conversionKey);
                                                            redisExpireDTO.getKeyList().add(conversionKey);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            //推送过期key队列
                            if (null != redisExpireDTO && redisExpireDTO.getKeyList().size() > 0) {
                                kafkaHelper.sendMQ(expireRedisTopic, redisExpireDTO
                                        , result -> log.info(logPrefix + "redis清除缓存key发送MQ成功, result={}", result)
                                        , ex -> {
                                            log.error(logPrefix + "redis清除缓存key发送MQ失败, 消息 info={}, ex={}", redisExpireDTO, ex);
                                            //todo 2019-6-5 15:04:46 这里考虑降级比如存入DB表中、由定时任务扫表去触发清除动作
                                        }
                                );
                            }
                        }

                    }
                    //todo 不开启jvm缓存 查数据库 2019-6-4 16:15:44
                } else {
                    log.info(logPrefix + ", redis_mapping未配置该表, 忽略消息, message={}", message);
                }
            }
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error(logPrefix + "消费异常, e={}", e);
        }
    }
}
