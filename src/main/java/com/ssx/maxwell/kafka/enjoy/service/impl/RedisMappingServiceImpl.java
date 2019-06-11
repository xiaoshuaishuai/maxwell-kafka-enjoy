package com.ssx.maxwell.kafka.enjoy.service.impl;

import com.ssx.maxwell.kafka.enjoy.common.model.entity.RedisMappingEntity;
import com.ssx.maxwell.kafka.enjoy.mapper.RedisMappingMapper;
import com.ssx.maxwell.kafka.enjoy.service.RedisMappingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/6 10:27
 * @description:
 */
@Service
@Slf4j
public class RedisMappingServiceImpl extends EnjoyBaseServiceImpl<RedisMappingEntity, RedisMappingMapper> implements RedisMappingService {
    @Autowired
    private RedisMappingMapper redisMappingMapper;

    @PostConstruct
    public void init() {
        super.mapper = redisMappingMapper;
    }
}
