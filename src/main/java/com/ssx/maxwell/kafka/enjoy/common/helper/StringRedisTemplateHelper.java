package com.ssx.maxwell.kafka.enjoy.common.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ssx.maxwell.kafka.enjoy.common.tools.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
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

    public String getValue(String key){
        log.info("start redis get key={}", key);
        String value = customerStringRedisTemplate.opsForValue().get(key);
        log.info("end redis get key={}, value={}", key, value);
        return value;
    }

}
