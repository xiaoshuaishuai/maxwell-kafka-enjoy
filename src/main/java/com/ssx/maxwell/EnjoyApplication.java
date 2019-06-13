package com.ssx.maxwell;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/3 17:37
 * @description:
 */
@SpringBootApplication
@MapperScan("com.ssx.maxwell.kafka.enjoy.mapper")
//fixme 排除 es redis 启动加载配置  2019-6-13 22:26:50
//@ComponentScan()
public class EnjoyApplication {
    static {
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }

    public static void main(String[] args) {
        SpringApplication.run(EnjoyApplication.class, args);
    }
}
