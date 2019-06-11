package com.ssx.maxwell.kafka.enjoy.consumer.redis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ssx.maxwell.kafka.enjoy.common.model.dto.RedisExpireAndLoadDTO;
import com.ssx.maxwell.kafka.enjoy.common.tools.JsonUtils;
import com.ssx.maxwell.kafka.enjoy.consumer.KafkaHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

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
    @Autowired
    private KafkaHelper kafkaHelper;
    @Value("${maxwell.enjoy.redis.reload-topic}")
    private String loadRedisTopic;

    @KafkaListener(topics = "${maxwell.enjoy.redis.expire-topic}", groupId = "${maxwell.enjoy.redis.expire-topic-kafka-group}", containerFactory = "manualListenerContainerFactory")
    public void receive(List<ConsumerRecord<String, String>> integerStringConsumerRecords, Acknowledgment acknowledgment) {
        log.info(logPrefix + ", integerStringConsumerRecords={}", integerStringConsumerRecords);
        try {
            for (ConsumerRecord<String, String> consumerRecord : integerStringConsumerRecords) {
                String keys = consumerRecord.value();
                log.info(logPrefix + "keys={}", keys);
                List<RedisExpireAndLoadDTO> redisExpireDTOS = JsonUtils.getMapper().readValue(keys, new TypeReference<List<RedisExpireAndLoadDTO>>() {
                });
                log.info(logPrefix + "redisExpireDTOS={}", redisExpireDTOS);
                if (null != redisExpireDTOS && redisExpireDTOS.size() > 0) {
                    List<String> stringList = redisExpireDTOS.stream().map(RedisExpireAndLoadDTO::getKey).collect(Collectors.toList());
                    log.info(logPrefix + "redis清除keys ={}", stringList);
                    customerStringRedisTemplate.delete(stringList);
//                    List<RedisExpireAndLoadDTO> collects = redisExpireDTOS.stream().distinct().collect(Collectors.toList());

                    List<RedisExpireAndLoadDTO> collects = redisExpireDTOS.stream()
                            .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(r -> r.getDbDatabase() + r.getDbTable()))), ArrayList::new));

                    log.info(logPrefix + "通知主动加载缓存collects={}", collects);
                    //通知主动加载缓存
                    kafkaHelper.sendMQ(loadRedisTopic, collects
                            , result -> log.info(logPrefix + "redis重载缓存发送MQ成功, result={}", result)
                            , ex -> {
                                //在查询处去做兼容缓存没有去查库
                                log.error(logPrefix + "redis重载缓存发送MQ失败, 消息 info={}, ex={}", redisExpireDTOS, ex);
                            }
                    );
                }
            }
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error(logPrefix + "消费异常, e={}", e);
        }
    }
}
