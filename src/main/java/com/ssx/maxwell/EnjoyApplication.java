package com.ssx.maxwell;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchProperties;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/3 17:37
 * @description:
 */
//默认关闭ES配置, if maxwell.enjoy.elasticsearch.kafka-consumer = true 这里必须 exclude需要打开配置es的自动装载否则报错
//@SpringBootApplication(exclude = {ElasticsearchAutoConfiguration.class})
@SpringBootApplication()
@MapperScan("com.ssx.maxwell.kafka.enjoy.mapper")
public class EnjoyApplication {
    static {
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }

    public static void main(String[] args) {
        SpringApplication.run(EnjoyApplication.class, args);
    }
}
