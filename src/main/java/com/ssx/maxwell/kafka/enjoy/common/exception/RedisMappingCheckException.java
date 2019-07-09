package com.ssx.maxwell.kafka.enjoy.common.exception;

/**
 * @author: shuaishuai.xiao
 * @date: 2019-7-9 16:01:29
 * @description: redis_mapping配置异常
 */
public class RedisMappingCheckException extends RuntimeException {
    public RedisMappingCheckException(String msg) {
        super(msg);
    }

    public RedisMappingCheckException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
