package com.ssx.maxwell.kafka.enjoy.common.helper;

import com.ssx.maxwell.kafka.enjoy.common.exception.RedissonWaitLockFailException;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/12 11:33
 * @description: no
 */
@Component
@Slf4j
public class RedissonHelper implements DistributedLock {

    @Autowired
    private RedissonClient redissonClient;

    @Value("${spring.application.name:maxwell-kafka-enjoy}")
    private String appName;

    @Value("${spring.profiles.active:dev}")
    private String profile;

    @Override
    public String buildKey(String path) {
        StringBuilder builder = new StringBuilder();
        builder.append(appName).append(":").append(profile).append(":").append(path);
        String name = builder.toString();
        return name;
    }

    private RLock rLock(String path) {
        String name = buildKey(path);
        return redissonClient.getLock(name);
    }

    @Override
    public void lock(String path, long waitTime, long leaseTime, TimeUnit unit, HandleData handleData) {
        RLock rLock = null;
        boolean isLock = false;
        try {
            rLock = rLock(path);
            isLock = rLock.tryLock(waitTime, leaseTime, unit);
            if (isLock) {
                handleData.handle();
            } else {
                throw new RedissonWaitLockFailException("wait lock fail , key =" + path);
            }
        } catch (InterruptedException e) {
            log.error("lock error, e={}", e);
        } finally {
            if (isLock && null != rLock && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }
    }
}
