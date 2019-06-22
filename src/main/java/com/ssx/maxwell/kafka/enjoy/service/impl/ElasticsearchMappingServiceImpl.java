package com.ssx.maxwell.kafka.enjoy.service.impl;

import com.ssx.maxwell.kafka.enjoy.common.model.bo.ElasticsearchMappingBO;
import com.ssx.maxwell.kafka.enjoy.common.model.db.ElasticsearchMappingDO;
import com.ssx.maxwell.kafka.enjoy.common.model.vo.ElasticsearchMappingVO;
import com.ssx.maxwell.kafka.enjoy.mapper.ElasticsearchMappingMapper;
import com.ssx.maxwell.kafka.enjoy.service.ElasticsearchMappingService;
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
public class ElasticsearchMappingServiceImpl extends EnjoyBaseServiceImpl<ElasticsearchMappingDO, ElasticsearchMappingVO, ElasticsearchMappingBO, ElasticsearchMappingMapper> implements ElasticsearchMappingService {
    @Autowired
    private ElasticsearchMappingMapper elasticSearchMappingMapper;

    @PostConstruct
    public void init() {
        super.mapper = elasticSearchMappingMapper;
    }

    @Override
    public ElasticsearchMappingVO newVO() {
        return new ElasticsearchMappingVO();
    }

    @Override
    public ElasticsearchMappingDO newDO() {
        return new ElasticsearchMappingDO();
    }
}
