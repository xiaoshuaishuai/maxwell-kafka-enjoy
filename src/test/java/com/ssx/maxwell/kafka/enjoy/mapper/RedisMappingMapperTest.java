package com.ssx.maxwell.kafka.enjoy.mapper;

import com.ssx.maxwell.kafka.enjoy.common.model.bo.RedisMappingBO;
import com.ssx.maxwell.kafka.enjoy.common.model.db.RedisMappingDO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/4 10:35
 * @description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisMappingMapperTest {

    @Autowired
    private RedisMappingMapper redisMappingMapper;


    @Test
    public void testQuery(){
        System.out.println(redisMappingMapper.list().toString());
    }


    @Test
    public void testInsert() {
        RedisMappingBO redisMapping = new RedisMappingBO();
        redisMapping.setPrimaryExpire(200L);
        redisMapping.setTableExpire(200L);
        redisMapping.setRule("1|2|3");
        redisMapping.setTemplate("{order_code}:{is_deleted}(2000),{goods_name}:{is_deleted}(2000)");
        redisMapping.setDbDatabase("test");
        redisMapping.setDbTable("sys_order");
        redisMapping.setEnable(0);
        redisMapping.setDeleted(0);
        redisMapping.setRemark("1.单表主键引导缓存\n" +
                "2.全表缓存\n" +
                "3.自定义缓存 {order_code}:{is_deleted}=订单号缓存" +
                "{goods_name}:{is_deleted}=商品名称缓存");
        redisMapping.setGmtCreate(new Date());
        redisMapping.setGmtModify(new Date());
        redisMappingMapper.insert(redisMapping);
    }
}