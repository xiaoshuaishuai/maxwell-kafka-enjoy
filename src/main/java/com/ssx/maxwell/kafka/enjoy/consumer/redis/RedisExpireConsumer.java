package com.ssx.maxwell.kafka.enjoy.consumer.redis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ssx.maxwell.kafka.enjoy.common.helper.KafkaHelper;
import com.ssx.maxwell.kafka.enjoy.common.model.dto.RedisExpireAndLoadDTO;
import com.ssx.maxwell.kafka.enjoy.common.tools.JsonUtils;
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
                        JsonUtils.getMapper().readValue(value, new TypeReference<RedisExpireAndLoadDTO>() {
                        });
                log.info(logPrefix + "redisExpireDTO={}", redisExpireDTO);
                try {
                    customerStringRedisTemplate.delete(redisExpireDTO.getKeyList());
                } catch (Exception redisException) {
                    //todo 2019-6-14 13:47:47 如果出现删除失败、redis连接等情况、将数据落入DB、由定时任务扫表进行清除动作、当然扫表有可能也失败同时增加预警
                    //总之为了尽快清除redis中的脏数据，存在的越久数据不一致也就越久
                    log.error(logPrefix + "清除redis key异常redisExpireDTO={}, e={}", redisExpireDTO, redisExpireDTO);

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
}
