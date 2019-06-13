package com.ssx.maxwell.kafka.enjoy.configuration;

import com.ssx.maxwell.kafka.enjoy.common.tools.ApplicationYamlUtils;
import com.ssx.maxwell.kafka.enjoy.common.tools.DynGenerateClassUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.beans.factory.support.AbstractBeanDefinition.AUTOWIRE_BY_NAME;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/13 14:25
 * @description: 动态注入bean
 */
@Component
@Slf4j
public class ServiceBeanDefinitionRegistry implements BeanDefinitionRegistryPostProcessor {

    /**
     * 目标类路径
     */
    public static final String DIST_PKG = "com.ssx.maxwell.kafka.enjoy.biz";
    /**
     * class 后缀
     */
    public static final String CLASS_SUFFIX = "BizImpl";

//    @Autowired
//    private DynamicDataSourceProperties dynamicDataSourceProperties;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
/*        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(MaxwellDatabaseBizImpl.class);
        beanDefinitionBuilder.addPropertyReference("jdbcTemplate", "jdbcTemplate");
        beanDefinitionBuilder.setAutowireMode(AUTOWIRE_BY_NAME);
        beanDefinitionRegistry.registerBeanDefinition("maxwellDatabaseBizImpl", beanDefinitionBuilder.getBeanDefinition());

        BeanDefinitionBuilder beanDefinitionBuilder2 = BeanDefinitionBuilder.rootBeanDefinition(BusinessTestDatabaseBizImpl.class);
        beanDefinitionBuilder2.addPropertyReference("jdbcTemplate", "jdbcTemplate");
        beanDefinitionBuilder2.setAutowireMode(AUTOWIRE_BY_NAME);
        beanDefinitionRegistry.registerBeanDefinition("businessTestDatabaseBizImpl", beanDefinitionBuilder2.getBeanDefinition());*/
//        DynamicDataSourceProperties dynamicDataSourceProperties = applicationContext.getBean(DynamicDataSourceProperties.class);
//        for(String s : applicationContext.getBeanDefinitionNames()){
//            System.out.println(s);
//        }
//        Map<String, DataSourceProperty> dataSourcePropertyMap = dynamicDataSourceProperties.getDatasource();
//        if (null != dataSourcePropertyMap && dataSourcePropertyMap.size() > 0) {
//            Set<String> keySet = dataSourcePropertyMap.keySet();
//            log.info("多数据源@DS keySet={}", keySet);
//            for (String key : keySet) {
//                dynamicDsInfos.add(new DynamicDsInfo(key));
//            }
//        }

        try {
            List<DynamicDsInfo> dbKeyLists = ApplicationYamlUtils.readDynamicConfig();
            if (null == dbKeyLists || dbKeyLists.isEmpty()) {
                log.warn("动态数据源配置为空=========");
                return;
            }
            for (DynamicDsInfo dynamicDsInfo : dbKeyLists) {
                //todo dskey 2019-6-13 19:09:56动态读取DynamicDataSourceProperties
                log.info("动态加载bean数据源配置信息, dynamicDsInfo={}", dynamicDsInfo);
                Class bizClass = DynGenerateClassUtils.createBizClass(DIST_PKG, dynamicDsInfo.getBizBeanName(), dynamicDsInfo.getDbKey());
                BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(bizClass);
                beanDefinitionBuilder.addPropertyReference("jdbcTemplate", "jdbcTemplate");
                beanDefinitionBuilder.setAutowireMode(AUTOWIRE_BY_NAME);
                beanDefinitionRegistry.registerBeanDefinition(dynamicDsInfo.getBizBeanName(), beanDefinitionBuilder.getBeanDefinition());
/*
                Class businessTestDatabaseBizImpl = DynGenerateClassUtils.createBizClass("com.ssx.maxwell.kafka.enjoy.biz", "BusinessTestDatabaseBizImpl", "business_test");
                BeanDefinitionBuilder beanDefinitionBuilder2 = BeanDefinitionBuilder.rootBeanDefinition(businessTestDatabaseBizImpl);
                beanDefinitionBuilder2.addPropertyReference("jdbcTemplate", "jdbcTemplate");
                beanDefinitionBuilder2.setAutowireMode(AUTOWIRE_BY_NAME);
                beanDefinitionRegistry.registerBeanDefinition("BusinessTestDatabaseBizImpl", beanDefinitionBuilder2.getBeanDefinition());*/
            }

        } catch (Exception e) {
            log.error("动态字节码生成DB层查询bean异常, e={}", e);
        }

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }
}
