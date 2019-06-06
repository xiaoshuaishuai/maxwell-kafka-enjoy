package com.ssx.maxwell.kafka.enjoy.consumer.redis;

import com.ssx.maxwell.kafka.enjoy.common.tools.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;

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
    private StringRedisTemplate customerStringRedisTemplate;

    @KafkaListener(topics = "${maxwell.enjoy.redis.expire-topic}", groupId = "${maxwell.enjoy.redis.expire-topic-kafka-group}", containerFactory = "manualListenerContainerFactory")
    public void receive(List<ConsumerRecord<String, String>> integerStringConsumerRecords, Acknowledgment acknowledgment) {
        log.info(logPrefix + ", integerStringConsumerRecords={}", integerStringConsumerRecords);
        try {
            for (ConsumerRecord<String, String> consumerRecord : integerStringConsumerRecords) {
                String keys = consumerRecord.value();
                //["dev:test:sys_order:item:2","dev:test:sys_order:custom:code3333333333:0","dev:test:sys_order:list","dev:test:sys_order:custom:\\u6d77\\u98de\\u4e1d\\u6d17\\u53d1\\u6c34:0"]
                log.info(logPrefix + "redis清除keys ={}", keys);
                HashSet<String> hashSetKey = JsonUtils.JsonStringToHashSet(keys);
                if (null != hashSetKey && hashSetKey.size() > 0) {
                    customerStringRedisTemplate.delete(hashSetKey);
//                    for (String s : hashSetKey) {
//                        log.info("===== {}", s);
//                    }
                }
            }
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error(logPrefix + "消费异常, e={}", e);
        }
    }
}
