package com.ssx.maxwell.kafka.enjoy.common.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/19 15:50
 * @description:
 */

@ApiModel
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class RespData<T> {
    @ApiModelProperty("响应码")
    private Integer code;
    @ApiModelProperty("响应消息")
    private String message;
    @ApiModelProperty("响应数据")
    private T data;
}
