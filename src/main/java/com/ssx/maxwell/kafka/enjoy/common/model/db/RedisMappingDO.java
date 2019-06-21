package com.ssx.maxwell.kafka.enjoy.common.model.db;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/4 09:52
 * @description: redis映射配置
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ToString(callSuper = true)
public class RedisMappingDO extends MappingDO {
    /**
     * 主键缓存
     * 缓存过期时间, 默认-1永不过期, 单位：秒
     */
    @ApiModelProperty(value = "缓存过期时间, 默认-1永不过期, 单位：秒")
    private Long primaryExpire;
    /**
     * 全表缓存
     * 缓存过期时间, 默认-1永不过期, 单位：秒
     */
    @ApiModelProperty(value = "缓存过期时间, 默认-1永不过期, 单位：秒")
    private Long tableExpire;
    /**
     * 缓存生成规则,可以配置多个,多个使用,分割, 1,2 ... 1,3 ...  1,2,3
     * 默认0.无缓存
     * 1.单表主键引导缓存
     * 2.全表缓存
     * 3.自定义缓存
     */
    @ApiModelProperty(value = "缓存生成规则,可以配置多个,多个使用,分割 1,2,3\n" +
            "默认0.无缓存\n" +
            "1.单表主键引导缓存\n" +
            "2.全表缓存\n" +
            "3.自定义缓存")
    private String rule;

    /**
     * 当rule包含3时,template, template ,分割
     * 自定义缓存模板
     * 默认0.无模板
     * 字段1:字段2:字段3(过期时间),
     * 字段1:字段2:字段3,字段1:字段2(过期时间)
     * 字段必须是table里对应的数据库字段,否则无法映射成功
     */
    @ApiModelProperty(value = "当rule包含3时,template, template ,分割\n" +
            "自定义缓存模板\n" +
            "默认0.无模板\n" +
            "字段1:字段2:字段3\n" +
            "字段1:字段2:字段3,字段1:字段2\n" +
            "字段必须是table里对应的数据库字段,否则无法映射成功")
    private String template;

    /**
     * 自定义模板对应sql,分割
     * 与template一一对应
     */
    @ApiModelProperty(value = "自定义模板对应sql,分割")
    private String templateSql;

}
