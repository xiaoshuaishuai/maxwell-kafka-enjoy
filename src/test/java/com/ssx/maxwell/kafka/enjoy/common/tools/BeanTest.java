package com.ssx.maxwell.kafka.enjoy.common.tools;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/13 11:13
 * @description: no
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class BeanTest {

    @Autowired
    private SpringContextUtils springContextUtils;

    @Test
    public void testBean() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Object object = springContextUtils.getBean("MaxwellDatabaseBizImpl");
        Class cls = object.getClass();
        Method method = cls.getMethod(DynGenerateClassUtils.BIZ_DEFAULT_METHOD_NAME, new Class[]{String.class});
        System.out.println(method.invoke(object, "select * from redis_mapping"));


        Object o2 = springContextUtils.getBean("BusinessTestDatabaseBizImpl");
        Class cls2 = o2.getClass();
        Method method2 = cls2.getMethod(DynGenerateClassUtils.BIZ_DEFAULT_METHOD_NAME, new Class[]{String.class});
        System.out.println(method2.invoke(o2, "select * from sys_order"));
    }

}