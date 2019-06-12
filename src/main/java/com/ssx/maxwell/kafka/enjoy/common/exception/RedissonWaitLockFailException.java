package com.ssx.maxwell.kafka.enjoy.common.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/12 13:04
 * @description: Redisson加锁等待失败异常
 */
@Data
@AllArgsConstructor
public class RedissonWaitLockFailException extends RuntimeException {
    private String msg;
}
