package com.ssx.maxwell.kafka.enjoy;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/3 17:23
 * @description:
 */
@Configuration
@ConfigurationProperties(prefix = "maxwell")
@PropertySource(value = "classpath:application.yml")
@Data
public class MaxwellProperties {
    private String kafkaBinlogTopic;
}
