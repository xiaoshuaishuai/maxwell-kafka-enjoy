package com.ssx.maxwell.kafka.enjoy.configuration;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/5 16:05
 * @description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisConfigTest {

    @Autowired
    private StringRedisTemplate customerStringRedisTemplate;

    @Test
    public void testRedis() {
        customerStringRedisTemplate.opsForValue().set("aaaa", "aaaa");
        Assert.assertEquals(customerStringRedisTemplate.opsForValue().get("aaaa"), "aaaa");
    }

}