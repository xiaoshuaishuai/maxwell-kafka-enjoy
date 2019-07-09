package com.ssx.maxwell.kafka.enjoy.service.impl;

import com.ssx.maxwell.kafka.enjoy.common.model.bo.RedisMappingBO;
import com.ssx.maxwell.kafka.enjoy.common.model.datao.RedisMappingDO;
import com.ssx.maxwell.kafka.enjoy.common.model.vo.RedisMappingVO;
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
public class RedisMappingServiceImpl extends EnjoyBaseServiceImpl<RedisMappingDO, RedisMappingVO, RedisMappingBO, RedisMappingMapper> implements RedisMappingService {
    @Autowired
    private RedisMappingMapper redisMappingMapper;

    @PostConstruct
    public void init() {
        super.mapper = redisMappingMapper;
    }

    @Override
    public RedisMappingDO getByDatabaseAndTable(RedisMappingBO redisMappingBO) {
        return redisMappingMapper.getByDatabaseAndTable(redisMappingBO);
    }

    @Override
    public RedisMappingVO newVO() {
        return new RedisMappingVO();
    }

    @Override
    public RedisMappingDO newDO() {
        return new RedisMappingDO();
    }
}
