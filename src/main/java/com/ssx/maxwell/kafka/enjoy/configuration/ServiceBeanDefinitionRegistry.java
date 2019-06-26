package com.ssx.maxwell.kafka.enjoy.configuration;

import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.hikari.HikariCpConfig;
import com.google.common.base.Strings;
import com.ssx.maxwell.kafka.enjoy.common.model.db.DynamicDatasourceDO;
import com.ssx.maxwell.kafka.enjoy.common.tools.ApplicationYamlUtils;
import com.ssx.maxwell.kafka.enjoy.common.tools.DynGenerateClassUtils;
import com.ssx.maxwell.kafka.enjoy.common.tools.JsonUtils;
import com.ssx.maxwell.kafka.enjoy.mapper.DynamicDataSourceMapper;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.beans.factory.support.AbstractBeanDefinition.AUTOWIRE_BY_NAME;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/13 14:25
 * @description: 动态注入bean
 */
@Component
@Slf4j
public class ServiceBeanDefinitionRegistry implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware {

    /**
     * 目标类路径
     */
    public static final String DIST_PKG = "com.ssx.maxwell.kafka.enjoy.biz";
    /**
     * class 后缀
     */
    public static final String CLASS_SUFFIX = "BizImpl";

    public static List<DynamicDsInfo> DYNAMIC_DS_INFO_LIST;

    private ApplicationContext applicationContext;

    @Autowired
    private DynamicDataSourceMapper dynamicDataSourceMapper;

    public static final String HIKARI_DATASOURCE = "com.zaxxer.hikari.HikariDataSource";
    public static final int HIKARI_DATASOURCE_INT = 0;


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
//         DynamicDataSourceProperties dynamicDataSourceProperties = applicationContext.getBean(DynamicDataSourceProperties.class);
//         for(String s : applicationContext.getBeanDefinitionNames()){
//             System.out.println(s);
//         }
//         Map<String, DataSourceProperty> dataSourcePropertyMap = dynamicDataSourceProperties.getDatasource();
//         if (null != dataSourcePropertyMap && dataSourcePropertyMap.size() > 0) {
//             Set<String> keySet = dataSourcePropertyMap.keySet();
//             log.info("多数据源@DS keySet={}", keySet);
//             for (String key : keySet) {
//                 dynamicDsInfos.add(new DynamicDsInfo(key));
//             }
//         }

        try {
            DynamicDataSourceProperties dynamicDataSourceProperties = applicationContext.getBean(DynamicDataSourceProperties.class);
            for (String s : applicationContext.getBeanDefinitionNames()) {
                System.out.println(s);
            }
            List<DynamicDatasourceDO> dynamicDataSourceDOS = dynamicDataSourceMapper.list();
            Map<String, DataSourceProperty> dataSourcePropertyMap = new HashMap<>();
            for (DynamicDatasourceDO dataSourceDO : dynamicDataSourceDOS) {
                DataSourceProperty dataSourceProperty = new DataSourceProperty();
                BeanUtils.copyProperties(dataSourceDO, dataSourceProperty);
                //fixme druid
                if (HIKARI_DATASOURCE_INT == dataSourceDO.getPoolType() && !Strings.isNullOrEmpty(dataSourceDO.getPoolConfig())) {
                    HikariCpConfig hikariCpConfig = (HikariCpConfig) JsonUtils.JsonStringToObject(dataSourceDO.getPoolConfig());
                    dataSourceProperty.setType(HikariDataSource.class);
                    dataSourceProperty.setHikari(hikariCpConfig);
                }
                dataSourcePropertyMap.put("business_" + dataSourceDO.getDbDatabase(), dataSourceProperty);
            }
            dynamicDataSourceProperties.setDatasource(dataSourcePropertyMap);

//            dynamicDataSourceProperties.setDatasource();
            //todo 动态数据库配置信息，这里改为从数据库配置，不从yml读取 2019-6-22 21:36:50

            List<DynamicDsInfo> dbKeyLists = ApplicationYamlUtils.readDynamicConfig();
            if (null == dbKeyLists || dbKeyLists.isEmpty()) {
                log.warn("动态数据源配置为空=========");
                return;
            }
            DYNAMIC_DS_INFO_LIST = new ArrayList<>(dbKeyLists);
//             [2019-06-14 11:13:10:801][maxwell-kafka-enjoy][INFO ] 10616 [com.ssx.maxwell.kafka.enjoy.configuration.ServiceBeanDefinitionRegistry.postProcessBeanDefinitionRegistry](70) : -- 动态加载bean数据源配置信息, dynamicDsInfo=DynamicDsInfo(dbKey=maxwell, database=maxwell, bizBeanName=maxwellBizImpl, cls=com.ssx.maxwell.kafka.enjoy.biz.maxwellBizImpl)
//[2019-06-14 11:13:16:969][maxwell-kafka-enjoy][INFO ] 10616 [com.ssx.maxwell.kafka.enjoy.configuration.ServiceBeanDefinitionRegistry.postProcessBeanDefinitionRegistry](70) : -- 动态加载bean数据源配置信息, dynamicDsInfo=DynamicDsInfo(dbKey=business_test, database=test, bizBeanName=businessTestBizImpl, cls=com.ssx.maxwell.kafka.enjoy.biz.businessTestBizImpl)
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

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
