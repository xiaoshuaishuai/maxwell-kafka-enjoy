package com.ssx.maxwell.kafka.enjoy.configuration;

import com.google.common.collect.Lists;
import com.ssx.maxwell.kafka.enjoy.common.tools.DynGenerateClassUtils;
import com.ssx.maxwell.kafka.enjoy.common.tools.JsonUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
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
//@ConditionalOnBean(value = {DynamicDataSourceAutoConfiguration.class, DynamicDataSourceProperties.class})
public class ServiceBeanDefinitionRegistry implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware {

    private ApplicationContext applicationContext;

    List<DynamicDsInfo> dynamicDsInfos = Lists.newArrayList();

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
            //todo dskey 2019-6-13 19:09:56动态读取DynamicDataSourceProperties
            Class master = DynGenerateClassUtils.createBizClass("com.ssx.maxwell.kafka.enjoy.biz", "MaxwellDatabaseBizImpl", "master");
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(master);
            beanDefinitionBuilder.addPropertyReference("jdbcTemplate", "jdbcTemplate");
            beanDefinitionBuilder.setAutowireMode(AUTOWIRE_BY_NAME);
            beanDefinitionRegistry.registerBeanDefinition("MaxwellDatabaseBizImpl", beanDefinitionBuilder.getBeanDefinition());

            Class businessTestDatabaseBizImpl = DynGenerateClassUtils.createBizClass("com.ssx.maxwell.kafka.enjoy.biz", "BusinessTestDatabaseBizImpl", "business_test");
            BeanDefinitionBuilder beanDefinitionBuilder2 = BeanDefinitionBuilder.rootBeanDefinition(businessTestDatabaseBizImpl);
            beanDefinitionBuilder2.addPropertyReference("jdbcTemplate", "jdbcTemplate");
            beanDefinitionBuilder2.setAutowireMode(AUTOWIRE_BY_NAME);
            beanDefinitionRegistry.registerBeanDefinition("BusinessTestDatabaseBizImpl", beanDefinitionBuilder2.getBeanDefinition());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Setter
    @Getter
    static class DynamicDsInfo {

        private String dsKey;
        private String bizBeanName;

        public DynamicDsInfo(String dsKey) {
            this.dsKey = dsKey;
            this.bizBeanName = getBizBeanName(dsKey);
        }

        public String getBizBeanName(String dsKey) {
            return JsonUtils.lineToHump(dsKey) + "Biz";
        }

    }
}
