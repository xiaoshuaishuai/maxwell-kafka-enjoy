package com.ssx.maxwell.kafka.enjoy.common.helper;

import com.ssx.maxwell.kafka.enjoy.common.exception.RedissonWaitLockFailException;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/12 11:37
 * @description: no
 */
public interface DistributedLock {

    /**
     * 功能描述: 锁 路径
     * @param: [path]
     * @return: java.lang.String
     * @author: shuaishuai.xiao
     * @date: 2019/6/12 13:07
     */
    String buildKey(String path);

    /**
     * 功能描述: 加锁
     * @param: [path, waitTime, leaseTime, unit, handleData]
     * @return: void
     * @author: shuaishuai.xiao
     * @date: 2019/6/12 13:08
     */
    void lock(String path, long waitTime, long leaseTime, TimeUnit unit, HandleData handleData) throws RedissonWaitLockFailException, UnsupportedEncodingException;

    @FunctionalInterface
    interface HandleData {
        void handle() throws UnsupportedEncodingException;
    }
}
