package com.ssx.maxwell.kafka.enjoy.biz.database.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/13 13:41
 * @description: no
 */
public class BusinessTestDatabaseBizImpl{
    @Setter
    @Getter
    org.springframework.jdbc.core.JdbcTemplate JdbcTemplate;

    @DS("business_test")
    public List queryForList(String sql) {
        return JdbcTemplate.queryForList(sql);
    }
}
