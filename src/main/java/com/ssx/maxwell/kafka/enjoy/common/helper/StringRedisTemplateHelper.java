package com.ssx.maxwell.kafka.enjoy.common.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ssx.maxwell.kafka.enjoy.common.tools.ApplicationYamlUtils;
import com.ssx.maxwell.kafka.enjoy.common.tools.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/14 14:25
 * @description:
 */
@Component
@Slf4j
public class StringRedisTemplateHelper {

    @Autowired
    private StringRedisTemplate customerStringRedisTemplate;

    private static final String REDIS_SENTINEL_MASTER_CONFIG_PROPERTY = "spring.redis.sentinel.master";
    private static final String REDIS_SENTINEL_NODES_CONFIG_PROPERTY = "spring.redis.sentinel.nodes";

    private static final String REDIS_CLUSTER_NODES_CONFIG_PROPERTY = "spring.redis.cluster.nodes";
    private static final String REDIS_CLUSTER_MAX_REDIRECTS_CONFIG_PROPERTY = "spring.redis.cluster.max-redirects";

    public void set(String key, Object value) throws JsonProcessingException {
        String v = JsonUtils.ObjectToJsonString(value);
        log.info("redis set-- key={}, value={}", key, v);
        customerStringRedisTemplate.opsForValue().set(key, v);
    }

    public void set(String key, Object value, long timeout, TimeUnit unit) throws JsonProcessingException {
        String v = JsonUtils.ObjectToJsonString(value);
        log.info("redis set-- key={}, value={}, timeout={}, unit={}", key, v, timeout, unit);
        customerStringRedisTemplate.opsForValue().set(key, v, timeout, unit);
    }

    public Long delete(Collection<String> keys) {
        log.info("redis delete={}", keys);
        return customerStringRedisTemplate.delete(keys);
    }

    public Long likeDelete(Collection<String> keys) {
        log.info("redis 模糊删除 delete={}", keys);


        if(ApplicationYamlUtils.isCluster()){
            //todo 集群模式下的模糊删除
            return null;
        }else if(ApplicationYamlUtils.isSentinel()){
            //todo 哨兵模式下的模糊删除
            return null;
        }else {
            Collection<String> fuzzySet = new HashSet<>();
            for(String fuzzyKey : keys){
                //单机模式模糊删除
                Set<String> set  = customerStringRedisTemplate.execute((RedisCallback<Set<String>>) connection -> {
                    Set<String> binaryKeys = new HashSet<>();
                    Cursor<byte[]> cursor = connection.scan( new ScanOptions.ScanOptionsBuilder().match(fuzzyKey).count(1000).build());
                    while (cursor.hasNext()) {
                        binaryKeys.add(new String(cursor.next()));
                    }
                    return binaryKeys;
                });
                fuzzySet.addAll(set);
            }
            log.info("redis 模糊删除组装之后 delete={}", fuzzySet);
            return customerStringRedisTemplate.delete(fuzzySet);
        }
    }

    public String getValue(String key){
        log.info("start redis get key={}", key);
        String value = customerStringRedisTemplate.opsForValue().get(key);
        log.info("end redis get key={}, value={}", key, value);
        return value;
    }

}
