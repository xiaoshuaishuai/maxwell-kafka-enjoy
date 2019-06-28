package com.ssx.maxwell.kafka.enjoy.enumerate;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/28 15:13
 * @description:
 */
public class SqlConstants {
    /**
     * 加载所有可用数据源
     */
    public static final String LOAD_DS_SQL = "SELECT id, db_database, pool_name, pool_config, driver_class_name, url, username, `password`, jndi_name FROM `dynamic_datasource` WHERE is_enable = 0 and is_deleted = 0 GROUP BY db_database;";
    /**
     * 加载数据库名称
     */
    public static final String LOAD_DS_NAME_SQL = "SELECT db_database FROM `dynamic_datasource` WHERE is_enable = 0 and is_deleted = 0 GROUP BY db_database;";

}
