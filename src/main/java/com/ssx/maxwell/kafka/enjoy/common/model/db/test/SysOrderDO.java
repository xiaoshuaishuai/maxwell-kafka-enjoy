package com.ssx.maxwell.kafka.enjoy.common.model.db.test;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author shuaishuai.xiao
 * @create 2019-06-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysOrderDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 订单号 */
    private String orderCode;

    /** 0/正常订单,1/促销订单 */
    private Integer category;

    /** 商品名称 */
    private String goodsName;

    /** 是否发货0/未 ,1/已发货 */
    private Integer sendExpress;

    /** 0/保留,1/删除 */
    private Integer deleted;

    /** 创建时间 */
    private Date gmtCreate;

    /** 修改时间 */
    private Date gmtModify;

}
