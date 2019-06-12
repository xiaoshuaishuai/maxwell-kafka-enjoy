package com.ssx.maxwell.kafka.enjoy.common.model.dto;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

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
    private List<String> keyList = Lists.newArrayList();

    /**
     * 数据库
     */
    private String dbDatabase;
    /**
     * 表
     */
    private String dbTable;
    /**
     * 目标表的主键id
     */
    private Long dbPid;


}
