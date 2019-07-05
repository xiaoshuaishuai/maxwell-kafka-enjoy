package com.ssx.maxwell.kafka.enjoy.common.tools;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Maps;
import com.ssx.maxwell.kafka.enjoy.biz.database.BusinessTestDatabaseBizImpl;
import com.ssx.maxwell.kafka.enjoy.biz.database.MaxwellDatabaseBizImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    public void testDb() throws JsonProcessingException {
        List l1 = maxwellDatabaseBiz.queryForList("select * from redis_mapping");
        System.out.println("=====,++++++" + l1);
        List<Map<String, Object>> cacheList = new ArrayList();
        for (Object object : l1) {
            Map<String, Object> map = (Map<String, Object>) object;
            Map<String, Object> cacheMap = Maps.newHashMap();
            Set<String> fieldSet = map.keySet();
            for (String field : fieldSet) {
                cacheMap.put(StringUtils.lineToHump(field), map.get(field));
            }
            cacheList.add(cacheMap);
        }
        System.out.println("map转json + ====" + JsonUtils.ObjectToJsonString(l1));
        System.out.println("map转json + 转换驼峰后 ====" + JsonUtils.ObjectToJsonString(cacheList));

        List l2 = businessTestDatabaseBiz.queryForList("select * from sys_order");
        System.out.println("=====,++++++" + l2);
    }

}