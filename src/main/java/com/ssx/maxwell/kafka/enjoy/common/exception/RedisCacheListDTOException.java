package com.ssx.maxwell.kafka.enjoy.common.exception;

/**
 * @author: shuaishuai.xiao
 * @date: 2019-7-5 11:14:56
 * @description: RedisCacheListDTO异常
 */
public class RedisCacheListDTOException extends RuntimeException {
    public RedisCacheListDTOException(String msg) {
        super(msg);
    }

    public RedisCacheListDTOException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
