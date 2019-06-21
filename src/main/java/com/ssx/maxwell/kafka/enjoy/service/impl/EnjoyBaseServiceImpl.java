package com.ssx.maxwell.kafka.enjoy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.ssx.maxwell.kafka.enjoy.mapper.EnjoyBaseMapper;
import com.ssx.maxwell.kafka.enjoy.service.EnjoyBaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/6 10:27
 * @description:
 */
@Slf4j
public abstract class EnjoyBaseServiceImpl<D, V, B, M extends EnjoyBaseMapper<D, B>> extends ServiceImpl<M, D> implements EnjoyBaseService<D, V, B> {

    EnjoyBaseMapper mapper;

    @Override
    public List<D> list() {
        return baseMapper.list();
    }

    @Override
    public List<V> listToV() {
        List<D> list = list();
        if (!CollectionUtils.isEmpty(list)) {
            List<V> resList = new ArrayList<>(list.size());
            for (D d : list) {
                V v = newVO();
                BeanUtils.copyProperties(d, v);
                resList.add(v);
            }
            return resList;
        }
        return Lists.newArrayList();
    }

    @Override
    public Integer insertOrUpdateBatch(List<B> list) {
//         List<D> resList = new ArrayList<>(list.size());
//         for (B bo : list) {
//             D d = newDO();
//             BeanUtils.copyProperties(bo, d);
//             resList.add(d);
//         }
        //todo jvmcache refresh 2019-6-6 16:52:31
        List<List<B>> parts = Lists.partition(list, 30);
        try {
            parts.stream().forEach(partList -> baseMapper.insertOrUpdateBatch(partList));
        } catch (Exception inExc) {
            log.error("批量异常, e={}", inExc);
            return 0;
        }
        return 1;
    }
}
