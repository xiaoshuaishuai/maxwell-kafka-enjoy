package com.ssx.maxwell.kafka.enjoy.configuration;


import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/5 11:01
 * @description:
 */
@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.consumer.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${spring.kafka.consumer.max-poll-records}")
    private Integer maxPollRecordsConfig;
    @Value("${spring.kafka.consumer.enable-auto-commit}")
    private boolean enableAutoCommit;
    @Value("${spring.kafka.consumer.session-timeout-ms}")
    private String sessionTimeoutMs;

    @Value("${spring.kafka.producer.acks}")
    private String acks;
    @Value("${spring.kafka.producer.batch-size}")
    private Integer batchSize;
    @Value("${spring.kafka.producer.linger-ms}")
    private Integer lingerMs;

    /**
     * 功能描述: 自定义使用kafka原生配置
     * @param: []
     * @return: java.util.Map<java.lang.String,java.lang.Object>
     * @author: shuaishuai.xiao
     * @date: 2019/6/5 11:12
     */
    private Map<String, Object> consumerProperties(){
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, sessionTimeoutMs);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecordsConfig);
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return props;
    }
    @Bean("consumerFactory")
    public DefaultKafkaConsumerFactory consumerFactory(){
        return new DefaultKafkaConsumerFactory(consumerProperties());
    }
    /**
     * 功能描述: 手动确认消息消费者工厂
     * @param: [consumerFactory]
     * @return: org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
     * @author: shuaishuai.xiao
     * @date: 2019/6/5 11:18
     */
    @Bean("manualListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory manualListenerContainerFactory(DefaultKafkaConsumerFactory consumerFactory) {
        //指定使用DefaultKafkaConsumerFactory
        ConcurrentKafkaListenerContainerFactory factory = new ConcurrentKafkaListenerContainerFactory();
        factory.setConsumerFactory(consumerFactory);
        //设置消费者ack模式为手动，看需求设置
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        //并发的消费者数量
        factory.setConcurrency(2);
        //批量监听器
        factory.setBatchListener(true);
        return factory;
    }
    /**
     * 功能描述: 自定义生产者配置
     * @param: []
     * @return: java.util.Map<java.lang.String,java.lang.Object>
     * @author: shuaishuai.xiao
     * @date: 2019/6/5 11:19
     */
    private Map<String, Object> producerProperties(){
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        至少有一个副本写成功
        props.put(ProducerConfig.ACKS_CONFIG, acks);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize);
//        延迟
        props.put(ProducerConfig.LINGER_MS_CONFIG, lingerMs);
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return props;
    }
    @Bean("produceFactory")
    public DefaultKafkaProducerFactory produceFactory(){
        return new DefaultKafkaProducerFactory(producerProperties());
    }
    @Bean
    public KafkaTemplate kafkaTemplate(DefaultKafkaProducerFactory produceFactory){
        return new KafkaTemplate(produceFactory);
    }
}
