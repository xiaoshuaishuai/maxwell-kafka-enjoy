package com.ssx.maxwell.kafka.enjoy.consumer.redis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ssx.maxwell.kafka.enjoy.common.model.dto.RedisExpireAndLoadDTO;
import com.ssx.maxwell.kafka.enjoy.common.tools.JsonUtils;
import com.ssx.maxwell.kafka.enjoy.common.helper.KafkaHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

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
    @Autowired
    private KafkaHelper kafkaHelper;
    @Value("${maxwell.enjoy.redis.reload-topic}")
    private String loadRedisTopic;

    @KafkaListener(topics = "${maxwell.enjoy.redis.expire-topic}", groupId = "${maxwell.enjoy.redis.expire-topic-kafka-group}", containerFactory = "manualListenerContainerFactory")
    public void receive(List<ConsumerRecord<String, String>> integerStringConsumerRecords, Acknowledgment acknowledgment) {
        log.info(logPrefix + ", integerStringConsumerRecords={}", integerStringConsumerRecords);
        try {
            for (ConsumerRecord<String, String> consumerRecord : integerStringConsumerRecords) {
                String value = consumerRecord.value();
                log.info(logPrefix + "value={}", value);
                RedisExpireAndLoadDTO redisExpireDTO =
                        JsonUtils.getMapper().readValue(value, new TypeReference<RedisExpireAndLoadDTO>(){} );
                log.info(logPrefix + "redisExpireDTO={}", redisExpireDTO);
                customerStringRedisTemplate.delete(redisExpireDTO.getKeyList());
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
}
