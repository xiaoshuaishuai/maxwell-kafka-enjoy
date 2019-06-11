package com.ssx.maxwell.kafka.enjoy.common.model.entity.test;

import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.annotations.TableId;
import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 
 * @author shuaishuai.xiao
 * @create 2019-06-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysOrder implements Serializable {

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
    private Integer isSendExpress;

    /** 0/保留,1/删除 */
    private Integer isDel;

    /** 创建时间 */
    private Date gmtCreate;

    /** 修改时间 */
    private Date gmtModify;

}
