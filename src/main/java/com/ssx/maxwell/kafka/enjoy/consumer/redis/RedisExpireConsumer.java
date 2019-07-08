package com.ssx.maxwell.kafka.enjoy.consumer.redis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Strings;
import com.ssx.maxwell.kafka.enjoy.common.helper.KafkaHelper;
import com.ssx.maxwell.kafka.enjoy.common.helper.StringRedisTemplateHelper;
import com.ssx.maxwell.kafka.enjoy.common.model.dto.RedisExpireAndLoadDTO;
import com.ssx.maxwell.kafka.enjoy.common.tools.JsonUtils;
import com.ssx.maxwell.kafka.enjoy.common.tools.TemplateUtils;
import com.ssx.maxwell.kafka.enjoy.service.RedisMappingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private StringRedisTemplateHelper stringRedisTemplateHelper;
    @Autowired
    private RedisMappingService redisMappingService;
    @Autowired
    private KafkaHelper kafkaHelper;
    @Value("${maxwell.enjoy.redis.reload-topic}")
    private String loadRedisTopic;
    @Value("${spring.profiles.active:dev}")
    private String profile;

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
                    /*Map oldDataJson = redisExpireDTO.getOldDataJson();
                    if (null != oldDataJson && !oldDataJson.isEmpty()) {
                        List<String> fuzzyKeyList = Lists.newArrayList();
                        RedisMappingBO redisMappingBO = new RedisMappingBO();
                        BeanUtils.copyProperties(redisExpireDTO, redisMappingBO);
                        RedisMappingDO redisMapping = redisMappingService.getByDatabaseAndTable(redisMappingBO);
                        if (!Strings.isNullOrEmpty(redisMapping.getRule()) && redisMapping.getRule().contains(MaxwellBinlogConstants.REDIS_RULE_3)
                                && !Strings.isNullOrEmpty(redisMapping.getTemplate())) {
                            String templates = redisMapping.getTemplate();
                            String[] templateArray = templates.split(",");
                            if (null != templateArray && templateArray.length > 0) {
                                for (String temp : templateArray) {
                                    if (!Strings.isNullOrEmpty(columnFilter(temp, oldDataJson))) {
                                        //模糊key 不包含过期时间
                                        String assembly = assemblyRedisKey(temp, oldDataJson);
                                        if(!Strings.isNullOrEmpty(assembly)){
                                            String redisKey = MessageFormat.format(MaxwellBinlogConstants.RedisCacheKeyTemplateEnum.REDIS_CACHE_KEY_TEMPLATE_PREFIX_CUSTOM.getTemplate(), profile, redisMapping.getDbDatabase(), redisMapping.getDbTable());
                                            fuzzyKeyList.add(redisKey + assembly);
                                            RedisExpireAndLoadDTO.ReloadKeyDTO reloadKeyDTO = new RedisExpireAndLoadDTO.ReloadKeyDTO();
                                            reloadKeyDTO.setTemplates(temp);
                                            //将过期时间绑定上
                                            reloadKeyDTO.setFuzzyKey(assembly + temp.substring(temp.indexOf("(")));
                                            redisExpireDTO.getReloadKeyDTOS().add(reloadKeyDTO);
                                        }
                                    }
                                }
                            }
                        }
                        if(!CollectionUtils.isEmpty(fuzzyKeyList)){
                            //模糊删除
                            stringRedisTemplateHelper.likeDelete(fuzzyKeyList);
                        }
                    }*/
                    log.info("redisExpireDTO.getKeyList()={}", redisExpireDTO.getDeleteKeyList());
                    stringRedisTemplateHelper.delete(redisExpireDTO.getDeleteKeyList());
                } catch (Exception redisException) {
                    //todo 2019-6-14 13:47:47 如果出现删除失败、redis连接等情况、将数据落入DB、由定时任务扫表进行清除动作、当然扫表有可能也失败同时增加预警
                    //总之为了尽快清除redis中的脏数据，存在的越久数据不一致也就越久
                    log.error(logPrefix + "清除redis key异常redisExpireDTO={}, e={}", redisExpireDTO, redisExpireDTO);
                    throw redisException;
                }
                //首先清除缓存、避免db和redis数据不一致、当然清除也会出现不一致、mq消费的越晚延迟越高、
                //通知主动加载缓存
                //主动加载缓存不订阅清除缓存的队列的reason是因为
                //如果使用当前队列需要有前后依赖，当清除以后在能触发reload，如果reload在delete就会有问题
                kafkaHelper.sendMQ(loadRedisTopic, redisExpireDTO
                        , result -> log.info(logPrefix + "reload缓存发送MQ成功, result={}", result)
                        , ex -> {
                            //在查询处去做兼容缓存没有去查库
                            log.error(logPrefix + "reload缓存发送MQ失败, 消息 redisExpireDTO={}, ex={}", redisExpireDTO, ex);
                        }
                );
            }
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error(logPrefix + "消费异常, e={}", e);
        }
    }

    /**
     * 过滤该模板有无存在修改的字段、如果存在修改的字段将该模板返回、如果不包含返回空，证明该模板相关的自定义缓存不需要清除
     *
     * @param template
     * @param oldDataJson
     * @return
     */
    private String columnFilter(String template, Map oldDataJson) {
        Set<String> keySet = oldDataJson.keySet();
        for (String column : keySet) {
            if (template.contains(column)) {
                return template;
            }
        }
        return null;
    }

    private String assemblyRedisKey( String template, Map oldDataJson) throws UnsupportedEncodingException {
        //:order_code:is_deleted(1800)
        //:goods_name:is_deleted(3600)
        //自定义缓存相关的key，可能包含模糊key
        String[] columnArray = template.split(":");
        if (null != columnArray && columnArray.length > 0) {
            StringBuilder keyStringBuilder = new StringBuilder();
            for(String column : columnArray){
                if(Strings.isNullOrEmpty(column)){
                    continue;
                }
                keyStringBuilder.append(":");
                if(column.contains("(")){
                    //筛选掉过期时间配置
                    column = column.substring(0, column.indexOf("("));
                }
                if(oldDataJson.containsKey(column)){
                    Object dbObj = oldDataJson.get(column);
                    TemplateUtils.encodeRedisKeySuffix(dbObj, keyStringBuilder);
                }else {
                    keyStringBuilder.append(column + "");
                }
            }
            String[] keyStringBuilderArray = keyStringBuilder.toString().split(":");
            StringBuilder keyStringBuilderArrayAfter = new StringBuilder();
            for (int i = 0; i < columnArray.length; i++) {
                String column = columnArray[i];
                if(Strings.isNullOrEmpty(column)){
                    continue;
                }
                keyStringBuilderArrayAfter.append(":");
                if(column.contains("(")){
                    //筛选掉过期时间配置
                    column = column.substring(0, column.indexOf("("));
                }
                if(column.equals(keyStringBuilderArray[i])){
                    //fixme 这里采用比较暴力的方式去清除key，这里存在误杀的情况，比如修改了is_del = 1, 那所有自定义key包含is_del=0相关的key都被删掉，最极端情况。
                    //如果去找具体相关的key去清除逻辑太过复杂
                    keyStringBuilderArray[i] = "*";
                }
                keyStringBuilderArrayAfter.append(keyStringBuilderArray[i]);
            }

            String fuzzyKey = keyStringBuilderArrayAfter.toString();
            log.info("当前需要清除的自定义key={}", fuzzyKey);
            return fuzzyKey;
        }
        return null;
    }
}

//            Map<String, Object> dbObjectMap = null;
//            String jdbcSql = MessageFormat.format(MaxwellBinlogConstants.RedisRunSqlTemplateEnum.SQL_PRIMARY_ID.getTemplate(), redisMapping.getDbTable(), dbPid);
//            String redisKey = MessageFormat.format(MaxwellBinlogConstants.RedisCacheKeyTemplateEnum.REDIS_CACHE_KEY_TEMPLATE_PREFIX_CUSTOM.getTemplate(), profile, redisMapping.getDbDatabase(), redisMapping.getDbTable());
//            log.info("sql= {} , key= {}", jdbcSql, redisKey);
//            List<Map<String, Object>> dbDataList = beanHelper.queryDbList(beanHelper.loopGetDynamicDsInfo(redisMapping.getDbDatabase()), jdbcSql);
//            if (null == dbDataList || dbDataList.isEmpty()) {
//                log.warn("自定义缓存查询数据库集合为空, 不进行构建, redisMapping={}", redisMapping);
//            } else {
//                dbObjectMap = dbDataList.get(0);
//            }
//            StringBuilder keyBuilder = new StringBuilder();
//            for (int k = 0; k < columnArray.length; k++) {
//                String field = null;
//                if (k == columnArray.length - 1) {
//                    //：最后一位过滤掉(过期时间)这部分内容
//                    field = columnArray[k].substring(0, columnArray[k].indexOf("("));
//                } else {
//                    field = columnArray[k];
//                }
//                if (!Strings.isNullOrEmpty(field)) {
//                    keyBuilder.append(":");
//                    //从old json 修改的字段取值
//                    if(oldDataJson.containsKey(field)){
//                        if(oldDataJson.get(field) instanceof String){
//                            beanHelper.appendRedisKeySuffix((String) oldDataJson.get(field), keyBuilder);
//                        }else {
//                            beanHelper.appendRedisKeySuffix(String.valueOf(oldDataJson.get(field)), keyBuilder);
//                        }
//                    }else {
//                        //不存在证明该字段未做修改， 从数据库取
//                        Object dbObj = dbObjectMap.get(field);
//                        beanHelper.appendRedisKeySuffix(dbObj, keyBuilder);
//                    }
//                }
//            }
//            keyList.add(redisKey+ keyBuilder.toString());

