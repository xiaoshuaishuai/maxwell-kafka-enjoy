package com.ssx.maxwell.kafka.enjoy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/6 18:22
 * @description: no
 */
public interface EnjoyBaseMapper<D, B> extends BaseMapper<D> {
    List<D> list();

    Integer insertOrUpdateBatch(List<B> list);
}
