package com.ssx.maxwell;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchProperties;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/3 17:37
 * @description:
 */
//fixme 动态配置不需要修改源码那种 启动配置类
@SpringBootApplication(exclude={
		ElasticsearchAutoConfiguration.class,
        ElasticsearchRepositoriesAutoConfiguration.class
})
@MapperScan("com.ssx.maxwell.kafka.enjoy.mapper")
public class EnjoyApplication {
    static {
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }

    public static void main(String[] args) {
        SpringApplication.run(EnjoyApplication.class, args);
    }
}
