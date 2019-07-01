package com.ssx.maxwell.kafka.enjoy.common.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author: shuaishuai.xiao
 * @date: 2019-6-14 13:20:22
 * @description:
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RedisCacheListDTO<T extends Serializable> implements Serializable {
    /**
     * 1.如果第一次从redis读取数据为null,
     * 那么调用mapper查询db返回CacheBo对象,如果此时DB中也不存在,那么isNone=true
     * 2.如果第一读取redis返回的对象为 CacheBo 但是  isNone = true,此时就不要再查库了！
     */
    private boolean isNone = false;

    private List<T> obj;

}