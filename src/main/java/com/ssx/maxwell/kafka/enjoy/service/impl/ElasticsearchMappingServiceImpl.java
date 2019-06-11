package com.ssx.maxwell.kafka.enjoy.service.impl;

import com.ssx.maxwell.kafka.enjoy.common.model.entity.ElasticsearchMapping;
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
public class ElasticsearchMappingServiceImpl extends EnjoyBaseServiceImpl<ElasticsearchMapping, ElasticsearchMappingMapper> implements ElasticsearchMappingService {
    @Autowired
    private ElasticsearchMappingMapper elasticSearchMappingMapper;

    @PostConstruct
    public void init() {
        super.mapper = elasticSearchMappingMapper;
    }
}
