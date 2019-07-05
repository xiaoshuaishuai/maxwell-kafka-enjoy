package com.ssx.maxwell.kafka.enjoy.common.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author: shuaishuai.xiao
 * @date: 2019-7-5 11:14:56
 * @description: RedisCacheListDTO异常
 */
@Data
@AllArgsConstructor
public class RedisCacheListDTOException extends RuntimeException {
    private String msg;
}
