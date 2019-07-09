package com.ssx.maxwell.kafka.enjoy.common.helper;

import com.ssx.maxwell.kafka.enjoy.common.tools.DynGenerateClassUtils;
import com.ssx.maxwell.kafka.enjoy.common.tools.SpringContextUtils;
import com.ssx.maxwell.kafka.enjoy.configuration.DynamicDsInfo;
import com.ssx.maxwell.kafka.enjoy.configuration.ServiceBeanDefinitionRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/28 17:51
 * @description:
 */
@Component
@Slf4j
public class BeanHelper {

    @Autowired
    private SpringContextUtils springContextUtils;

    /**
     * 通过具体数据源bean执行SQL
     * @param dynamicDsInfo
     * @param sql
     * @return
     */
    public List<Map<String, Object>> queryDbList(DynamicDsInfo dynamicDsInfo, String sql) {
        try {
            Object object = springContextUtils.getBean(dynamicDsInfo.getBizBeanName());
            Class cls = object.getClass();
            Method method = cls.getMethod(DynGenerateClassUtils.BIZ_DEFAULT_METHOD_NAME, new Class[]{String.class});
            List<Map<String, Object>> dbDataList = (List<Map<String, Object>>) method.invoke(object, sql);
            log.info("数据库源数据返回,dynamicDsInfo={}, sql={},list={}", dynamicDsInfo, sql, dbDataList);
            return dbDataList;
        } catch (Exception e) {
            log.error("反射执行BIZ出错,e=", e);
        }
        return null;
    }

    /**
     * 功能描述: 获取数据源相关信息
     *
     * @param: [dbDatabase]
     * @return: com.ssx.maxwell.kafka.enjoy.configuration.DynamicDsInfo
     * @author: shuaishuai.xiao
     * @date: 2019/6/14 16:57
     */
    public static DynamicDsInfo loopGetDynamicDsInfo(String dbDatabase) {
        List<DynamicDsInfo> dynamicDsInfoList = ServiceBeanDefinitionRegistry.DYNAMIC_DS_INFO_LIST;
        if (null == dynamicDsInfoList || dynamicDsInfoList.isEmpty()) {
            log.error("动态数据源BIZ加载失败、bean集合为空");
        }
        for (DynamicDsInfo d : dynamicDsInfoList) {
            if (null != d && dbDatabase.equals(d.getDatabase())) {
                return d;
            }
        }
        return null;
    }
}
