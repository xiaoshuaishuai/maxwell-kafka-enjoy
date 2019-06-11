package com.ssx.maxwell.kafka.enjoy.common.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/11 15:33
 * @description: no
 */
@Data
@EqualsAndHashCode
public class RedisExpireAndLoadDTO {
    /**
     * 过期key
     */
    private String key;

    /**
     * 数据库
     */
    @EqualsAndHashCode.Include
    private String dbDatabase;
    /**
     * 表
     */
    @EqualsAndHashCode.Include
    private String dbTable;

    /**
     * 创建时间
     */
    private Date gmtCreate;


}
