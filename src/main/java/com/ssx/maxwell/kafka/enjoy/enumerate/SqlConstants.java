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
    /**
     * 按照主键id查询
     *
     */
    public static final String SQL_PRIMARY_ID = "SELECT * FROM {0} WHERE ID = {1}";
    /**
     * //todo 全表查询缓存- 大表慎用  --大表不建议开启全表缓存-- 没意义  后面考虑支持分页缓存
     * 全表查询- 大表慎用-- order by gmt_modify desc
     */
    public static final String SQL_ALL = "SELECT * FROM {0} {1} LIMIT 100000";
    /**
     * 自定义缓存SQL
     */
    public static final String SQL_CUSTOM = "SELECT * FROM {0} WHERE {1} {2} LIMIT 100000";

}
