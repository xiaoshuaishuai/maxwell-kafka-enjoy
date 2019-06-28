package com.ssx.maxwell.kafka.enjoy.configuration;

import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.hikari.HikariCpConfig;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
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
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
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
public class ServiceBeanDefinitionRegistry extends InstantiationAwareBeanPostProcessorAdapter implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware {

    /**
     * 目标类路径
     */
    public static final String DIST_PKG = "com.ssx.maxwell.kafka.enjoy.biz";
    /**
     * class 后缀
     */
    public static final String CLASS_SUFFIX = "BizImpl";
    /**
     * 动态数据源信息
     */
    public static List<DynamicDsInfo> DYNAMIC_DS_INFO_LIST;

    private ApplicationContext applicationContext;

    @Autowired
    private DynamicDataSourceMapper dynamicDataSourceMapper;
    /**
     * com.zaxxer.hikari.HikariDataSource
     */
    public static final int HIKARI_DATASOURCE_INT = 0;


    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        try {
//            DynamicDataSourceProperties dynamicDataSourceProperties = applicationContext.getBean(DynamicDataSourceProperties.class);
//            List<DynamicDatasourceDO> dynamicDataSourceDOS = dynamicDataSourceMapper.list();
//            Map<String, DataSourceProperty> dataSourcePropertyMap = new HashMap<>();
//            for (DynamicDatasourceDO dataSourceDO : dynamicDataSourceDOS) {
//                DataSourceProperty dataSourceProperty = new DataSourceProperty();
//                BeanUtils.copyProperties(dataSourceDO, dataSourceProperty);
//                //fixme druid
//                if (HIKARI_DATASOURCE_INT == dataSourceDO.getPoolType() && !Strings.isNullOrEmpty(dataSourceDO.getPoolConfig())) {
//                    HikariCpConfig hikariCpConfig = (HikariCpConfig) JsonUtils.JsonStringToObject(dataSourceDO.getPoolConfig());
//                    dataSourceProperty.setType(HikariDataSource.class);
//                    dataSourceProperty.setHikari(hikariCpConfig);
//                }
//                dataSourcePropertyMap.put("business_" + dataSourceDO.getDbDatabase(), dataSourceProperty);
//            }
//            dynamicDataSourceProperties.setDatasource(dataSourcePropertyMap);
//            List<DynamicDsInfo> dbKeyLists = ApplicationYamlUtils.readDynamicConfig();
            List<DynamicDsInfo> dbKeyLists = Lists.newArrayList();
            dbKeyLists.add(new DynamicDsInfo("maxwell"));
            dbKeyLists.add(new DynamicDsInfo("test"));
            if (null == dbKeyLists || dbKeyLists.isEmpty()) {
                log.warn("动态数据源配置为空=========");
                return;
            }
            DYNAMIC_DS_INFO_LIST = new ArrayList<>(dbKeyLists);
            for (DynamicDsInfo dynamicDsInfo : dbKeyLists) {
                //todo dskey 2019-6-13 19:09:56动态读取DynamicDataSourceProperties
                log.info("动态加载bean数据源配置信息, dynamicDsInfo={}", dynamicDsInfo);
                Class bizClass = DynGenerateClassUtils.createBizClass(DIST_PKG, dynamicDsInfo.getBizBeanName(), dynamicDsInfo.getDbKey());
                BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(bizClass);
                beanDefinitionBuilder.addPropertyReference("jdbcTemplate", "jdbcTemplate");
                beanDefinitionBuilder.setAutowireMode(AUTOWIRE_BY_NAME);
                beanDefinitionRegistry.registerBeanDefinition(dynamicDsInfo.getBizBeanName(), beanDefinitionBuilder.getBeanDefinition());
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

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return super.postProcessAfterInitialization(bean, beanName);
    }
}
