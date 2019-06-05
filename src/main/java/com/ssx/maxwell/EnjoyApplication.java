package com.ssx.maxwell;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/3 17:37
 * @description:
 */
@SpringBootApplication
@MapperScan("com.ssx.maxwell.kafka.enjoy.mapper")
public class EnjoyApplication {
    public static void main(String[] args) {
        SpringApplication.run(EnjoyApplication.class, args);
    }
}
