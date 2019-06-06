package com.ssx.maxwell.kafka.enjoy.common.model.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/4 10:16
 * @description: 映射
 */
@Data
@Accessors(chain = true)
public class MappingEntity implements Serializable {
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
    /**
     * 0:启用
     * 1:禁用
     */
    @ApiModelProperty(value = "0:启用/1:禁用")
    private Integer isEnable;
    /**
     * 0:保留
     * 1:删除
     */
    @ApiModelProperty(value = "0:保留/1:删除")
    private Integer isDel;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date gmtCreate;
    /**
     * 修改时间
     */
    @ApiModelProperty(value = "修改时间")
    private Date gmtModify;
    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private String createBy;
    /**
     * 修改人
     */
    @ApiModelProperty(value = "修改人")
    private String modifyBy;
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;
}
