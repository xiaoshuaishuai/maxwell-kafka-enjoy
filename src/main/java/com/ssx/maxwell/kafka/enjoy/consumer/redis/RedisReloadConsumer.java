package com.ssx.maxwell.kafka.enjoy.consumer.redis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ssx.maxwell.kafka.enjoy.common.model.dto.RedisExpireAndLoadDTO;
import com.ssx.maxwell.kafka.enjoy.common.tools.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author: shuaishuai.xiao
 * @date: 2019-6-11 16:41:11
 * @description: redis重载缓存
 */
@Component
@Slf4j
@ConditionalOnProperty(prefix = "maxwell.enjoy.redis", name = "expire-kafka-consumer", havingValue = "true")
public class RedisReloadConsumer {

    private static final String logPrefix = "maxwell--<redis重载缓存>--消费消息-->";
    @Autowired
    private StringRedisTemplate customerStringRedisTemplate;

    @KafkaListener(topics = "${maxwell.enjoy.redis.reload-topic}", groupId = "${maxwell.enjoy.redis.reload-topic-kafka-group}", containerFactory = "autoListenerContainerFactory")
    public void receive(List<ConsumerRecord<String, String>> integerStringConsumerRecords) {
        log.info(logPrefix + ", integerStringConsumerRecords={}", integerStringConsumerRecords);
        try {
            for (ConsumerRecord<String, String> consumerRecord : integerStringConsumerRecords) {
                String keys = consumerRecord.value();
                List<RedisExpireAndLoadDTO> redisExpireDTOS = JsonUtils.getMapper().readValue(keys, new TypeReference<List<RedisExpireAndLoadDTO>>() {
                });
                if (null != redisExpireDTOS && redisExpireDTOS.size() > 0) {
                    log.info(logPrefix + "重载缓存list={}", redisExpireDTOS);
                    //todo 加载缓存至redis
                }
            }
        } catch (Exception e) {
            log.error(logPrefix + "消费异常, e={}", e);
        }
    }
}
