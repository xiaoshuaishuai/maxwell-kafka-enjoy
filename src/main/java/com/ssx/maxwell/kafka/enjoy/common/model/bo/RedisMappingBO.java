package com.ssx.maxwell.kafka.enjoy.common.model.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/12 13:20
 * @description: no
 */
@Data
@Accessors(chain = true)
public class RedisMappingBO {
    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    private Long id;
    /**
     * 数据库
     */
    @ApiModelProperty(value = "数据库")
    private String dbDatabase;
    /**
     * 表
     */
    @ApiModelProperty(value = "表")
    private String dbTable;
}
