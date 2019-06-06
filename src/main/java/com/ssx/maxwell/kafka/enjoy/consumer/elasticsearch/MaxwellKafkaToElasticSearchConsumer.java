package com.ssx.maxwell.kafka.enjoy.consumer.elasticsearch;

import com.google.common.base.Strings;
import com.ssx.maxwell.kafka.enjoy.common.model.query.elasticsearch.ElasticSearchMapping;
import com.ssx.maxwell.kafka.enjoy.common.tools.JsonUtils;
import com.ssx.maxwell.kafka.enjoy.configuration.JvmCache;
import com.ssx.maxwell.kafka.enjoy.enumerate.MaxwellBinlogConstants;
import com.ssx.maxwell.kafka.enjoy.search.ElasticSearchMappingRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/3 14:07
 * @description: kafka transport elasticsearch
 */
@Component
@Slf4j
@ConditionalOnProperty(prefix = "maxwell.enjoy.elasticsearch", name = "kafka-consumer", havingValue = "true")
public class MaxwellKafkaToElasticSearchConsumer {
    private static final String logPrefix = "maxwell--<elasticsearch>----消费消息-->";
    @Value("${spring.profiles.active:dev}")
    private String profile;
    @Autowired
    private ElasticSearchMappingRepository elasticSearchMappingRepository;

    @KafkaListener(topics = "${maxwell.enjoy.kafka-binlog-topic}", groupId = "${maxwell.enjoy.elasticsearch.kafka-group}", containerFactory = "manualListenerContainerFactory")
    public void receive(List<ConsumerRecord<String, String>> integerStringConsumerRecords, Acknowledgment acknowledgment) {
        log.info(logPrefix + ", integerStringConsumerRecords={}", integerStringConsumerRecords);
        try {
            for (ConsumerRecord consumerRecord : integerStringConsumerRecords) {
                log.info(logPrefix + ", consumerRecord={}", consumerRecord);
                String message = (String) consumerRecord.value();
                Map<String, Object> map = null;
                try {
                    map = JsonUtils.JsonStringToMap(message);
                } catch (IOException e) {
                    throw e;
                }
                if (null != map && !map.isEmpty() && map.containsKey("database") && map.containsKey("table") && map.containsKey("type")) {
                    String type = (String) map.get("type");
                    if (!Strings.isNullOrEmpty(type)) {
                        MaxwellBinlogConstants.MaxwellBinlogEnum maxwellBinlogEnum = MaxwellBinlogConstants.MaxwellBinlogEnum.getMaxwellBinlogEnum(type);
                        if (null == maxwellBinlogEnum) {
                            log.error(logPrefix + "未识别的type, message={}", message);
                            return;
                        }
                        if (type.equals(MaxwellBinlogConstants.MaxwellBinlogEnum.INSERT.getType()) ||
                                type.equals(MaxwellBinlogConstants.MaxwellBinlogEnum.UPDATE.getType()) ||
                                type.equals(MaxwellBinlogConstants.MaxwellBinlogEnum.DELETE.getType()) ||
                                type.equals(MaxwellBinlogConstants.MaxwellBinlogEnum.BOOTSTRAP_INSERT.getType()) ||
                                type.equals(MaxwellBinlogConstants.MaxwellBinlogEnum.BOOTSTRAP_UPDATE.getType()) ||
                                type.equals(MaxwellBinlogConstants.MaxwellBinlogEnum.BOOTSTRAP_DELETE.getType())) {
                            String database = (String) map.get("database");
                            String table = (String) map.get("table");
                            Map dataJson = (Map) map.get("data");
                            Integer id = (Integer) dataJson.get("id");
                            String esId = buildEsId(database, table, id);
                            //todo 2019-6-6 17:11:01 MQ通知重构ES
                            log.info("MQ通知重构ES, id={}", esId);
                        }
                    }
                }
            }
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error(logPrefix + "消费异常, e={}", e);
        }
    }

    private String buildEsId(String database, String table, Integer id) {
        //dev#database#table#id
        return JvmCache.redisJvmCacheKey(profile, database, table) + JvmCache.BROKEN_WELL + id;
    }

    private void buildElasticSearchMapping(String id, String dbDatabase, String dbTable, String mkeData) {
        ElasticSearchMapping elasticSearchMapping = new ElasticSearchMapping();
        elasticSearchMapping.setId(id);
        elasticSearchMapping.setDbDatabase(dbDatabase);
        elasticSearchMapping.setDbTable(dbTable);
        elasticSearchMapping.setMkeData(mkeData);
        elasticSearchMapping.setGmtCreate(System.currentTimeMillis());
        elasticSearchMapping.setGmtModify(System.currentTimeMillis());
        elasticSearchMappingRepository.save(elasticSearchMapping);
    }
}