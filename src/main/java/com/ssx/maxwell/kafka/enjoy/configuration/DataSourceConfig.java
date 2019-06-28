package com.ssx.maxwell.kafka.enjoy.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/28 10:33
 * @description:
 */
@Configuration
public class DataSourceConfig {

    private String driverClassName = "com.mysql.jdbc.Driver";
    private String url = "jdbc:mysql://192.168.225.1:3306/maxwell?useUnicode=true&characterEncoding=UTF8&useSSL=false&allowMultiQueries=true&autoReconnect=true&failOverReadOnly=false&maxReconnects=10&tinyInt1isBit=false";
    private String username = "root";
    private String password = "root";

    @Bean
    public JdbcDynamicDataSourceProviderImpl jdbc() {
        return new JdbcDynamicDataSourceProviderImpl(driverClassName, url, username, password);
    }
}
