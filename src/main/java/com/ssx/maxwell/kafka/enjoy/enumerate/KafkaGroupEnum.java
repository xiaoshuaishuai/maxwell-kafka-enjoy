package com.ssx.maxwell.kafka.enjoy.enumerate;

import lombok.Getter;
import lombok.Setter;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/3 17:34
 * @description:
 */
public enum KafkaGroupEnum {
    REDIS("default-redis-group"),
    ELASTICSEARCH("default-elasticsearch-group"),
    ;

    KafkaGroupEnum(String value) {
        this.value = value;
    }

    @Setter
    @Getter
    public String value;
}
