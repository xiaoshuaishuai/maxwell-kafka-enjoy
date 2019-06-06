package com.ssx.maxwell.kafka.enjoy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.ssx.maxwell.kafka.enjoy.mapper.EnjoyBaseMapper;
import com.ssx.maxwell.kafka.enjoy.service.EnjoyBaseService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/6 10:27
 * @description:
 */
@Slf4j
public abstract class EnjoyBaseServiceImpl<T, M extends EnjoyBaseMapper<T>> extends ServiceImpl<M, T> implements EnjoyBaseService<T> {

    EnjoyBaseMapper mapper;

    @Override
    public List<T> queryList() {
        return baseMapper.queryList();
    }

    @Override
    public Integer insertOrUpdateBatch(List list) {
        //todo jvmcache refresh 2019-6-6 16:52:31
        List<List<T>> parts = Lists.partition(list, 30);
        try {
            parts.stream().forEach(partList -> baseMapper.insertOrUpdateBatch(partList));
        } catch (Exception inExc) {
            log.error("批量异常, e={}", inExc);
            return 0;
        }
        return 1;
    }
}
