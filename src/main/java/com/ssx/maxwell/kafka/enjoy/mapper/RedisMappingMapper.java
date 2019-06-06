package com.ssx.maxwell.kafka.enjoy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ssx.maxwell.kafka.enjoy.common.model.entity.redis.RedisMappingEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/4 10:34
 * @description:
 */
@Component
public interface RedisMappingMapper extends BaseMapper<RedisMappingEntity> {

    List<RedisMappingEntity> queryList();

    Integer insertOrUpdateBatch(List<RedisMappingEntity> list);
}
