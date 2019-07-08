package com.ssx.maxwell.kafka.enjoy.common.helper;

import com.ssx.maxwell.kafka.enjoy.common.model.db.test.SysOrderDO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/7/8 16:27
 * @description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class StringRedisTemplateHelperTest {
    @Autowired
    private StringRedisTemplateHelper helper;

    @Test
    public void test() {
        System.out.println("StringRedisTemplateHelperTest==" + helper.getValue("a"));
        System.out.println("StringRedisTemplateHelperTest==" + helper.getValue("b"));
    }

}