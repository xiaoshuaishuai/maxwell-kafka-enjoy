package com.ssx.maxwell.kafka.enjoy.biz.database.impl;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/13 13:42
 * @description: no
 */
public class MaxwellDatabaseBizImpl {

    @Setter
    @Getter
    public JdbcTemplate jdbcTemplate;

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
