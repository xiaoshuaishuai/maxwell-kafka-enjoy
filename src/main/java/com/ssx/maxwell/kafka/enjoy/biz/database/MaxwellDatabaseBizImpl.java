package com.ssx.maxwell.kafka.enjoy.biz.database;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/13 13:42
 * @description: no
 */
@Component("maxwellDatabaseBizImpl")
public class MaxwellDatabaseBizImpl {

    @Autowired
    public JdbcTemplate jdbcTemplate;

    @DS("maxwell")
    public List queryForList(String sql) {
        return jdbcTemplate.queryForList(sql);
    }
}
