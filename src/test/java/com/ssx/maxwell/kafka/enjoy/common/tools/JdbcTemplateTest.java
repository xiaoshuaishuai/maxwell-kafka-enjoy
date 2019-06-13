package com.ssx.maxwell.kafka.enjoy.common.tools;

import com.ssx.maxwell.kafka.enjoy.biz.database.impl.BusinessTestDatabaseBizImpl;
import com.ssx.maxwell.kafka.enjoy.biz.database.impl.MaxwellDatabaseBizImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/13 11:13
 * @description: no
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class JdbcTemplateTest {


    @Autowired
    @Qualifier(value = "maxwellDatabaseBizImpl")
    private MaxwellDatabaseBizImpl maxwellDatabaseBiz;
    @Autowired
    @Qualifier(value = "businessTestDatabaseBizImpl")
    private BusinessTestDatabaseBizImpl businessTestDatabaseBiz;

    @Test
    public void testDb() {
        List l1 = maxwellDatabaseBiz.getJdbcTemplate().queryForList("select * from redis_mapping");
        System.out.println("=====,++++++" + l1);
        List l2 = businessTestDatabaseBiz.queryForList("select * from sys_order");
        System.out.println("=====,++++++" + l2);
    }

}