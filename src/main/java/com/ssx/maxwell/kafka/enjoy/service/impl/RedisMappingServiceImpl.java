package com.ssx.maxwell.kafka.enjoy.service.impl;

import com.google.common.collect.Lists;
import com.ssx.maxwell.kafka.enjoy.common.redis.RedisMapping;
import com.ssx.maxwell.kafka.enjoy.mapper.RedisMappingMapper;
import com.ssx.maxwell.kafka.enjoy.service.RedisMappingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/6 10:27
 * @description:
 */
@Service
@Slf4j
public class RedisMappingServiceImpl implements RedisMappingService {

    @Autowired
    private RedisMappingMapper redisMappingMapper;

    @Override
    public List<RedisMapping> queryList() {
        return redisMappingMapper.queryList();
    }

    @Override
    public Integer insertOrUpdateBatch(List<RedisMapping> list) {
        list.forEach(t -> {
            t.setGmtCreate(new Date());
            t.setGmtModify(new Date());
        });

        List<List<RedisMapping>> parts = Lists.partition(list, 30);
        try {
            parts.stream().forEach(partList -> redisMappingMapper.insertOrUpdateBatch(partList));
        } catch (Exception inExc) {
            log.error("list:{}, exception:{}", list, inExc);
            return 0;
        }
        return 1;
    }
}
