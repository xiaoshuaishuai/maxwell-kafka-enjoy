package com.ssx.maxwell.kafka.enjoy.consumer.redis;

import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.ssx.maxwell.kafka.enjoy.common.helper.KafkaHelper;
import com.ssx.maxwell.kafka.enjoy.common.helper.StringRedisTemplateHelper;
import com.ssx.maxwell.kafka.enjoy.common.model.datao.RedisMappingDO;
import com.ssx.maxwell.kafka.enjoy.common.model.dto.RedisExpireAndLoadDTO;
import com.ssx.maxwell.kafka.enjoy.common.tools.JsonUtils;
import com.ssx.maxwell.kafka.enjoy.common.tools.TemplateUtils;
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
import org.springframework.util.CollectionUtils;

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
    @Autowired
    private StringRedisTemplateHelper stringRedisTemplateHelper;
    @Value("${maxwell.enjoy.redis.reload-topic}")
    private String loadRedisTopic;
    @Autowired
    private KafkaHelper kafkaHelper;
    //todo 动态消费者功能支持 maxwell配置为namespace_%{database}_%{table} # kafka topic to write to #kafka_topic=maxwell
    //# this can be static, e.g. 'maxwell', or dynamic, e.g. namespace_%{database}_%{table}
    //# in the latter case 'database' and 'table' will be replaced with the values for the row being processed
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
                                            redisExpireDTO.setDataJson(dataJson);
                                            if (ArrayUtils.contains(ruleArr, MaxwellBinlogConstants.REDIS_RULE_1)) {
                                                //处理单表主键缓存
                                                String redisKey = MessageFormat.format(MaxwellBinlogConstants.RedisCacheKeyTemplateEnum.REDIS_CACHE_KEY_TEMPLATE_ITEM_PK_ID.getTemplate(), profile, database, table, id);
                                                // id字段 修改
                                                if (null != oldDataJson && !oldDataJson.isEmpty() && oldDataJson.containsKey("id") && null != oldDataJson.get("id")) {
                                                    String key = MessageFormat.format(MaxwellBinlogConstants.RedisCacheKeyTemplateEnum.REDIS_CACHE_KEY_TEMPLATE_ITEM_PK_ID.getTemplate(), profile, database, table, oldDataJson.get("id") + "");
                                                    redisExpireDTO.getDeleteKeyList().add(key);
                                                    log.info(logPrefix + "处理单表主键缓存[id变更]redisKey={}", key);
                                                }
                                                redisExpireDTO.getDeleteKeyList().add(redisKey);
                                                log.info(logPrefix + "处理单表主键缓存redisKey={}", redisKey);
                                            }
                                            if (ArrayUtils.contains(ruleArr, MaxwellBinlogConstants.REDIS_RULE_2)) {
                                                //处理全表缓存
                                                String redisKey = MessageFormat.format(MaxwellBinlogConstants.RedisCacheKeyTemplateEnum.REDIS_CACHE_KEY_TEMPLATE_PREFIX_LIST.getTemplate(), profile, database, table);
                                                redisExpireDTO.getDeleteKeyList().add(redisKey);
                                                log.info(logPrefix + "处理全表缓存redisKey={}", redisKey);

                                            }
                                            if (ArrayUtils.contains(ruleArr, MaxwellBinlogConstants.REDIS_RULE_3)) {
                                                redisExpireDTO.setOldDataJson(oldDataJson);
                                                List<String> keySuffixList = TemplateUtils.templateConversionKey(redisMapping.getTemplate(), dataJson, oldDataJson);
                                                if(!CollectionUtils.isEmpty(keySuffixList)){
                                                    String redisKey = MessageFormat.format(MaxwellBinlogConstants.RedisCacheKeyTemplateEnum.REDIS_CACHE_KEY_TEMPLATE_PREFIX_CUSTOM.getTemplate(), profile, database, table);
                                                    for(String keySuffix : keySuffixList){
                                                        // :order_code:is_deleted
                                                        // :goods_name:is_deleted
                                                        String conversionKey = redisKey + keySuffix ;
                                                        log.info(logPrefix + "处理自定义缓存redisKey={}", conversionKey);
                                                        redisExpireDTO.getDeleteKeyList().add(conversionKey);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            //推送过期key队列
                            if (null != redisExpireDTO && redisExpireDTO.getDeleteKeyList().size() > 0) {
                                try {
                                    log.info("redisExpireDTO.getKeyList()={}", redisExpireDTO.getDeleteKeyList());
                                    stringRedisTemplateHelper.delete(redisExpireDTO.getDeleteKeyList());
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
                                kafkaHelper.sendMQ(loadRedisTopic, redisExpireDTO
                                        , result -> log.info(logPrefix + "重载缓存发送MQ成功, result={}", result)
                                        , ex -> {
                                            //在查询处去做兼容缓存没有去查库
                                            log.error(logPrefix + "重载缓存发送MQ失败, 消息 redisExpireDTO={}, ex={}", redisExpireDTO, ex);
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
