package com.ssx.maxwell.kafka.enjoy.consumer.redis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ssx.maxwell.kafka.enjoy.biz.RedisReloadBiz;
import com.ssx.maxwell.kafka.enjoy.common.model.dto.RedisExpireAndLoadDTO;
import com.ssx.maxwell.kafka.enjoy.common.tools.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author: shuaishuai.xiao
 * @date: 2019-6-11 16:41:11
 * @description: redis重载缓存
 */
@Component
@Slf4j
@ConditionalOnProperty(prefix = "maxwell.enjoy.redis", name = "kafka-consumer", havingValue = "true")
public class RedisReloadConsumer {

    private static final String logPrefix = "maxwell--<redis重载缓存>--消费消息-->";
    @Autowired
    private RedisReloadBiz redisReloadBiz;


    //     @KafkaListener(topics = "${maxwell.enjoy.redis.reload-topic}", groupId = "${maxwell.enjoy.redis.reload-topic-kafka-group}", containerFactory = "autoListenerContainerFactory")
    @KafkaListener(topics = "${maxwell.enjoy.redis.reload-topic}", groupId = "${maxwell.enjoy.redis.reload-topic-kafka-group}", containerFactory = "manualListenerContainerFactory")
    public void receive(List<ConsumerRecord<String, String>> integerStringConsumerRecords, Acknowledgment acknowledgment) {
        log.info(logPrefix + ", integerStringConsumerRecords={}", integerStringConsumerRecords);
        try {
            for (ConsumerRecord<String, String> consumerRecord : integerStringConsumerRecords) {
                String value = consumerRecord.value();
                RedisExpireAndLoadDTO redisExpireDTO =
                        JsonUtils.getMapper().readValue(value, new TypeReference<RedisExpireAndLoadDTO>() {
                        });
                log.info(logPrefix + "redisExpireDTO={}", redisExpireDTO);
                redisReloadBiz.reloadCache(redisExpireDTO.getDbDatabase(), redisExpireDTO.getDbTable(), redisExpireDTO.getDbPid(), redisExpireDTO.getDataJson(), redisExpireDTO.getOldDataJson());
            }
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error(logPrefix + "消费异常, e={}", e);
        }
    }
}
