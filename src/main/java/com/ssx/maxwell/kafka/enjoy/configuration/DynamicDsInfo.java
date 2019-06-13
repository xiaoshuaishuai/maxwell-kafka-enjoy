package com.ssx.maxwell.kafka.enjoy.configuration;

import com.ssx.maxwell.kafka.enjoy.common.tools.JsonUtils;
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
     * db_key
     */
    private String dbKey;
    /**
     * 数据库
     */
    private String database;
    /**
     * spring bean name
     */
    private String bizBeanName;
    /**
     * 类
     * com.xx.xx.AAA
     */
    private String cls;

    public DynamicDsInfo(String dbKey) {
        this.dbKey = dbKey;
        this.bizBeanName = getBizBeanName(dbKey);
        this.database = dbKey.substring(9);
        this.cls = ServiceBeanDefinitionRegistry.DIST_PKG + "." + bizBeanName;
    }

    public String getBizBeanName(String dsKey) {
        return JsonUtils.lineToHump(dsKey) + ServiceBeanDefinitionRegistry.CLASS_SUFFIX;
    }

}