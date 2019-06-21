package com.ssx.maxwell.kafka.enjoy.service.impl;

import com.ssx.maxwell.kafka.enjoy.service.RedisMappingService;
import com.ssx.maxwell.kafka.enjoy.service.SysOrderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/11 11:35
 * @description: no
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SysOrderServiceImplTest {

    @Autowired
    private SysOrderService sysOrderService;

    @Autowired
    private RedisMappingService redisMappingService;

    @Test
    public void testQuery() {
        System.out.println("测试多数据源: =" + sysOrderService.list().toString());
        System.out.println("测试多数据源: =" + redisMappingService.list().toString());
    }
}