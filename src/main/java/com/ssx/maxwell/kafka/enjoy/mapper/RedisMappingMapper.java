package com.ssx.maxwell.kafka.enjoy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ssx.maxwell.kafka.enjoy.common.redis.RedisMapping;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/4 10:34
 * @description:
 */
@Component
public interface RedisMappingMapper extends BaseMapper<RedisMapping> {

    List<RedisMapping> queryList();

    Integer insertOrUpdateBatch(List<RedisMapping> list);
}
