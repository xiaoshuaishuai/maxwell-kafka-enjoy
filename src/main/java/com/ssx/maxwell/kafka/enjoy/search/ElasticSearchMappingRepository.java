package com.ssx.maxwell.kafka.enjoy.search;

import com.ssx.maxwell.kafka.enjoy.common.model.query.elasticsearch.ElasticSearchMapping;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/6 15:44
 * @description:
 */
@Component
public interface ElasticSearchMappingRepository extends ElasticsearchRepository<ElasticSearchMapping, String> {
}
