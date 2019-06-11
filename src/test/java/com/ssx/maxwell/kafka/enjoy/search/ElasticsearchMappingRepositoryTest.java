package com.ssx.maxwell.kafka.enjoy.search;

import com.ssx.maxwell.kafka.enjoy.common.model.query.ElasticsearchMapping;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/6 15:45
 * @description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ElasticsearchMappingRepositoryTest {

    @Autowired
    private ElasticsearchMappingRepository elasticSearchMappingRepository;

    @Test
    public void testEsSave(){
        ElasticsearchMapping elasticSearchMapping = new ElasticsearchMapping();
        elasticSearchMapping.setId("1");
        elasticSearchMapping.setDbDatabase("test1");
        elasticSearchMapping.setDbTable("sys_order1");
        elasticSearchMapping.setMkeData("{\"database\":\"test1\",\"table\":\"sys_order1\",\"type\":\"update\",\"ts\":1559640375,\"xid\":2012,\"commit\":true,\"data\":{\"id\":1,\"order_code\":\"1\",\"category\":0,\"goods_name\":\"牙膏\",\"is_send_express\":1,\"is_del\":0,\"gmt_create\":\"2019-06-04 17:21:45\",\"gmt_modify\":\"2019-06-04 17:26:15\"},\"old\":{\"is_send_express\":0,\"gmt_modify\":\"2019-06-04 17:25:25\"}}");
        elasticSearchMapping.setGmtCreate(System.currentTimeMillis());
        elasticSearchMapping.setGmtModify(System.currentTimeMillis());
        elasticSearchMappingRepository.save(elasticSearchMapping);

    }
}