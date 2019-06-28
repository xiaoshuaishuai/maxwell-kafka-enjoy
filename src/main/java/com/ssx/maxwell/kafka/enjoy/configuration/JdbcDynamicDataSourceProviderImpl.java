package com.ssx.maxwell.kafka.enjoy.configuration;

import com.baomidou.dynamic.datasource.provider.AbstractJdbcDataSourceProvider;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.hikari.HikariCpConfig;
import com.google.common.collect.Maps;
import com.ssx.maxwell.kafka.enjoy.common.tools.JsonUtils;
import com.ssx.maxwell.kafka.enjoy.enumerate.SqlConstants;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/28 10:09
 * @description:
 */
@Slf4j
public class JdbcDynamicDataSourceProviderImpl extends AbstractJdbcDataSourceProvider {

    /**
     * DRUID数据源类
     */
    private static final String DRUID_DATASOURCE = "com.alibaba.druid.pool.DruidDataSource";
    /**
     * HikariCp数据源
     */
    private static final String HIKARI_DATASOURCE = "com.zaxxer.hikari.HikariDataSource";


    public JdbcDynamicDataSourceProviderImpl(String driverClassName, String url, String username, String password) {
        super(driverClassName, url, username, password);
    }

    @Override
    protected Map<String, DataSourceProperty> executeStmt(Statement statement) throws SQLException {
        Map<String, DataSourceProperty> resultMap = Maps.newHashMap();
        ResultSet rs = statement.executeQuery(SqlConstants.LOAD_DS_SQL);
        try {
            while (rs.next()) {
                String dbDatabase = rs.getString("db_database");
                String poolName = rs.getString("pool_name");
                String poolConfig = rs.getString("pool_config");
                String driverClassName = rs.getString("driver_class_name");
                String url = rs.getString("url");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String jndi_name = rs.getString("jndi_name");
                DataSourceProperty dataSourceProperty = new DataSourceProperty();
                dataSourceProperty.setPollName(dbDatabase);
                if (DRUID_DATASOURCE.equals(poolName)) {
//                    dataSourceProperty.setType(DruidDataSource.class);
//                    DruidConfig druidConfig = (DruidConfig) JsonUtils.JsonStringToObject(poolConfig);
//                    dataSourceProperty.setDruid(druidConfig);
                    //fixme 兼容druid
                    log.info("jdbc 构建数据源统一使用HikariDataSource");
                } else if (HIKARI_DATASOURCE.equals(poolName)) {
                    dataSourceProperty.setType(HikariDataSource.class);
                    HikariCpConfig hikariCpConfig = JsonUtils.getMapper().readValue(poolConfig, HikariCpConfig.class);
                    dataSourceProperty.setHikari(hikariCpConfig);
                }
                dataSourceProperty.setDriverClassName(driverClassName);
                dataSourceProperty.setUrl(url);
                dataSourceProperty.setUsername(username);
                dataSourceProperty.setPassword(password);
                dataSourceProperty.setJndiName(jndi_name);
                resultMap.put(dbDatabase, dataSourceProperty);

            }
        } catch (IOException e) {
            log.error("jdbc 构建数据源出错, e={}", e);
        }

        return resultMap;
    }
}
