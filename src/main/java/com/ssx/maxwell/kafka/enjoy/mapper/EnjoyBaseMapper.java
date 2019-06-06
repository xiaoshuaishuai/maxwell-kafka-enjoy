package com.ssx.maxwell.kafka.enjoy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/6 18:22
 * @description: no
 */
public interface EnjoyBaseMapper<T> extends BaseMapper<T> {
    List<T> queryList();
    Integer insertOrUpdateBatch(List<T> list);
}
