package com.ssx.maxwell.kafka.enjoy.service;

import com.ssx.maxwell.kafka.enjoy.common.redis.RedisMapping;

import java.util.List;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/6 10:23
 * @description:
 */
public interface RedisMappingService {

    List<RedisMapping> queryList();

    Integer insertOrUpdateBatch(List<RedisMapping> list);

}
