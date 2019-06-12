package com.ssx.maxwell.kafka.enjoy.service;

import com.ssx.maxwell.kafka.enjoy.common.model.bo.RedisMappingBO;
import com.ssx.maxwell.kafka.enjoy.common.model.entity.RedisMapping;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/6 10:23
 * @description:
 */
public interface RedisMappingService extends EnjoyBaseService<RedisMapping>{

    RedisMapping queryOneByDatabaseAndTable(RedisMappingBO redisMappingBO);
}
