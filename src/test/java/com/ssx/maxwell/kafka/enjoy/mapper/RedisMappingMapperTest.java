package com.ssx.maxwell.kafka.enjoy.mapper;

import com.ssx.maxwell.kafka.enjoy.common.model.entity.RedisMappingEntity;
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
        System.out.println(redisMappingMapper.queryList().toString());
    }


    @Test
    public void testInsert() {
        RedisMappingEntity redisMapping = new RedisMappingEntity();
        redisMapping.setExpire(200L);
        redisMapping.setRule("1|2|3");
        redisMapping.setTemplate("{order_code}:{is_del},{goods_name}:{is_del}");
        redisMapping.setDbDatabase("test");
        redisMapping.setDbTable("sys_order");
        redisMapping.setIsEnable(0);
        redisMapping.setIsDel(0);
        redisMapping.setRemark("1.单表主键引导缓存\n" +
                "2.全表缓存\n" +
                "3.自定义缓存 {order_code}:{is_del}=订单号缓存" +
                "{goods_name}:{is_del}=商品名称缓存");
        redisMapping.setGmtCreate(new Date());
        redisMapping.setGmtModify(new Date());
        redisMappingMapper.insert(redisMapping);
    }
}