package com.ssx.maxwell.kafka.enjoy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @author: shuaishuai.xiao
 * @date: 2019-6-22 21:51:00
 * @description: no
 */
public interface EnjoyBaseMapper<D, B> extends BaseMapper<D> {
    List<D> list();
    Integer insertOrUpdateBatch(List<B> list);
}
