package com.ssx.maxwell.kafka.enjoy.common.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ssx.maxwell.kafka.enjoy.common.tools.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SuccessCallback;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/11 15:58
 * @description: no
 */
@Component
@Slf4j
public class KafkaHelper {
    @Autowired
    private KafkaTemplate kafkaTemplate;

    public ListenableFuture<SendResult<String, String>> sendMQ(String topic, Object object, SuccessCallback successCallback, FailureCallback failureCallback) throws JsonProcessingException {
        ProducerRecord producerRecord = new ProducerRecord(topic, JsonUtils.ObjectToJsonString(object));
        log.info("kafka helper send mq , mq ={}", producerRecord);
        ListenableFuture<SendResult<String, String>> sendResultListenableFuture = kafkaTemplate.send(producerRecord);
        sendResultListenableFuture.addCallback(successCallback, failureCallback);
        return sendResultListenableFuture;
    }
    public ListenableFuture<SendResult<String, String>> sendMQ(String topic, String value, SuccessCallback successCallback, FailureCallback failureCallback) throws JsonProcessingException {
        ProducerRecord producerRecord = new ProducerRecord(topic, value);
        log.info("kafka helper send mq , mq ={}", producerRecord);
        ListenableFuture<SendResult<String, String>> sendResultListenableFuture = kafkaTemplate.send(producerRecord);
        sendResultListenableFuture.addCallback(successCallback, failureCallback);
        return sendResultListenableFuture;
    }
}
