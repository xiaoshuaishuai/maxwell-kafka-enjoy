package com.ssx.maxwell.kafka.enjoy.configuration;

import com.ssx.maxwell.kafka.enjoy.common.tools.ApplicationYamlUtils;
import com.ssx.maxwell.kafka.enjoy.common.tools.DynGenerateClassUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.beans.factory.support.AbstractBeanDefinition.AUTOWIRE_BY_NAME;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/13 14:25
 * @description: 动态注入bean
 */
@Component
@Slf4j
public class ServiceBeanDefinitionRegistry extends InstantiationAwareBeanPostProcessorAdapter implements BeanDefinitionRegistryPostProcessor {

    /**
     * 目标类路径
     */
    public static final String DIST_PKG = "com.ssx.maxwell.kafka.enjoy.biz";
    /**
     * class 后缀
     */
    public static final String CLASS_SUFFIX = "DatabaseBizImpl";
    /**
     * 动态数据源信息
     */
    public static List<DynamicDsInfo> DYNAMIC_DS_INFO_LIST;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        try {
            List<DynamicDsInfo> dbKeyLists = ApplicationYamlUtils.readDynamicConfig();
            if (null == dbKeyLists || dbKeyLists.isEmpty()) {
                log.warn("动态数据源配置为空=========");
                return;
            }
            DYNAMIC_DS_INFO_LIST = new ArrayList<>(dbKeyLists);
            for (DynamicDsInfo dynamicDsInfo : dbKeyLists) {
                log.info("动态加载bean数据源配置信息, dynamicDsInfo={}", dynamicDsInfo);
                Class bizClass = DynGenerateClassUtils.createBizClass(DIST_PKG, dynamicDsInfo.getClassName(), dynamicDsInfo.getDbKey());
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
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return super.postProcessAfterInitialization(bean, beanName);
    }
}
