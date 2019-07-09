package com.ssx.maxwell.kafka.enjoy.common.exception;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/12 13:04
 * @description: Redisson加锁等待失败异常
 */
public class RedissonWaitLockFailException extends RuntimeException {
    public RedissonWaitLockFailException(String msg) {
        super(msg);
    }
}
