package com.ssx.maxwell.kafka.enjoy.consumer.elasticsearch;

import com.ssx.maxwell.kafka.enjoy.MaxwellProperties;
import com.ssx.maxwell.kafka.enjoy.enumerate.KafkaConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/3 14:07
 * @description: kafka transport elasticsearch
 */
@Component
@Slf4j
@ConditionalOnProperty(prefix="maxwell.enjoy.elasticsearch",name = "kafka-consumer", havingValue = "true")
public class MaxwellKafkaToElasticSearchConsumer {
    @Autowired
    private MaxwellProperties maxwellProperties;

    @KafkaListener(topics = "${maxwell.enjoy.kafka-binlog-topic}", groupId = "${maxwell.enjoy.elasticsearch.kafka-group}")
    public void receive(String message) {
        //maxwell--消费消息:{"database":"test","table":"title","type":"update","ts":1559542729,"xid":2470,"commit":true,"data":{"id":2,"name":"2","content":"88"},"old":{"content":"2"}}
        //maxwell--消费消息:{"database":"test","table":"title","type":"update","ts":1559542747,"xid":2498,"commit":true,"data":{"id":2,"name":"2","content":"99"},"old":{"content":"88"}}
        //maxwell--消费消息:{"database":"test","table":"title","type":"insert","ts":1559542757,"xid":2515,"commit":true,"data":{"id":3,"name":"3","content":"199"}}
//        maxwell--消费消息:{"database":"test","table":"title","type":"bootstrap-start","ts":1559548626,"data":{}}
//        maxwell--消费消息:{"database":"test","table":"title","type":"bootstrap-insert","ts":1559548626,"data":{"id":1,"name":"1","content":"3"}}
//        maxwell--消费消息:{"database":"test","table":"title","type":"bootstrap-insert","ts":1559548626,"data":{"id":2,"name":"2","content":"3"}}
//        maxwell--消费消息:{"database":"test","table":"title","type":"bootstrap-insert","ts":1559548626,"data":{"id":3,"name":"3","content":"3"}}
//        maxwell--消费消息:{"database":"test","table":"title","type":"bootstrap-complete","ts":1559548626,"data":{}}
        log.info("maxwell--<elasticsearch>--消费消息:" + message);
    }
}
