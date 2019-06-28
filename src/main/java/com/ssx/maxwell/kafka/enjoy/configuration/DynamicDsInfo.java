package com.ssx.maxwell.kafka.enjoy.configuration;

import com.ssx.maxwell.kafka.enjoy.common.tools.StringUtils;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author: shuaishuai.xiao
 * @date: 2019-6-13 22:18:09
 * @description: 动态bean属性
 */
@Data
@Accessors(chain = true)
public class DynamicDsInfo {
    /**
     * db_key =  数据库
     */
    private String dbKey;
    /**
     * 数据库 =  db_key
     */
    private String database;
    /**
     * spring bean name
     */
    private String bizBeanName;
    /**
     * 类
     * AAA
     */
    private String className;
    /**
     * 类
     * com.xx.xx.AAA
     */
    private String cls;

    public DynamicDsInfo(String dbKey) {
        this.dbKey = dbKey;
        this.bizBeanName = getBizBeanName(dbKey);
        this.database = dbKey;
        this.cls = ServiceBeanDefinitionRegistry.DIST_PKG + "." + getClassName(bizBeanName);
        this.className = getClassName(bizBeanName);
    }

    /**
     * testDatasourceBizImpl
     *
     * @param dsKey
     * @return
     */
    public String getBizBeanName(String dsKey) {
        return StringUtils.lineToHump(dsKey) + ServiceBeanDefinitionRegistry.CLASS_SUFFIX;
    }

    /**
     * bizBeanName 首字母转大写
     * testDatasourceBizImpl 转 TestDatasourceBizImpl
     *
     * @param bizBeanName
     * @return
     */
    public String getClassName(String bizBeanName) {
        StringBuilder className = new StringBuilder();
        char zeroIndexChar = bizBeanName.charAt(0);
        className.append(String.valueOf(zeroIndexChar).toUpperCase());
        className.append(bizBeanName.substring(1));
        return className.toString();
    }

}