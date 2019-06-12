package com.ssx.maxwell.kafka.enjoy.configuration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/12 11:17
 * @description: no
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedissonClientTest {
    @Autowired
    private RedissonClient redissonClient;

    @Test
    public void test() {
        RLock lock = redissonClient.getLock("anyLock");
        boolean res;
        try {
            res = lock.tryLock(100, 10, TimeUnit.SECONDS);
            if (res) {
                try {
                    System.out.println("加锁成功====================");
                } finally {
                    lock.unlock();
                }
            }else {
                System.out.println("加锁失败====================");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
