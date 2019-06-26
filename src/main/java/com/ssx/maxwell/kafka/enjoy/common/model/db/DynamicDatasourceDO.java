package com.ssx.maxwell.kafka.enjoy.common.model.db;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author: shuaishuai.xiao
 * @date: 22019-6-22 21:47:59
 * @description: 动态数据源配置
 */
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
public class DynamicDatasourceDO {
    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    private Long id;
    /**
     * database name
     * //fixme conversion dbDataBase
     */
    @ApiModelProperty(value = "database name")
    private String dbDatabase;
    
    /**
     * 连接池类型，如果不设置自动查找 druid > hikari
     * //todo 连接池没有配置在数据库，默认采用hikari配置文件中的配置，可以考虑配置在库中用json字符串映射相关连接池
     * //fixme conversion dbDataBase
     */
    @ApiModelProperty(value = "连接池类型，如果不设置自动查找 1/druid  0/hikari 默认 0")
    private Integer poolType;
    
    /**
     * druid or hikari json配置
     */
    @ApiModelProperty(value = "druid or hikari json配置, 默认hikari")
    private String poolConfig;
    
    /**
     * JDBC driver
     */
    @ApiModelProperty(value = "JDBC driver")
    private String driverClassName;
    /**
     * JDBC url 地址
     */
    @ApiModelProperty(value = "JDBC url 地址")
    private String url;
    /**
     * JDBC 用户名
     */
    @ApiModelProperty(value = "JDBC 用户名")
    private String username;
    /**
     * JDBC 密码
     */
    @ApiModelProperty(value = "JDBC 密码")
    private String password;
    /**
     * jndi数据源名称(设置即表示启用)
     */
    @ApiModelProperty(value = "jndi数据源名称(设置即表示启用)")
    private String jndiName;
    /**
     * 0:启用
     * 1:禁用
     */
    @ApiModelProperty(value = "0:启用/1:禁用")
    private Integer enable;
    /**
     * 0:保留
     * 1:删除
     */
    @ApiModelProperty(value = "0:保留/1:删除")
    private Integer deleted;
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