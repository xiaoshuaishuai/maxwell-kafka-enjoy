package com.ssx.maxwell.kafka.enjoy.common.tools;

import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.hikari.HikariCpConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.ssx.maxwell.kafka.enjoy.common.model.dto.RedisExpireAndLoadDTO;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/4 14:58
 * @description:
 */
@Slf4j
public final class JsonUtils {
    @Getter
    private static final ObjectMapper mapper = new ObjectMapper();


    /**
     * 对象转string
     *
     * @param object
     * @return
     * @throws JsonProcessingException
     */
    public static final String ObjectToJsonString(Object object) throws JsonProcessingException {
        if (null == object) {
            return null;
        }
        return mapper.writeValueAsString(object);
    }

    public static final Map<String, Object> JsonStringToMap(String text) throws IOException {
        if (Strings.isNullOrEmpty(text)) {
            return null;
        }
        return mapper.readValue(text, Map.class);
    }


    public static void main(String[] args) throws IOException {

        String text = "{\"database\":\"test\",\"table\":\"title\",\"type\":\"update\",\"ts\":1559542729,\"xid\":2470,\"commit\":true,\"data\":{\"id\":2,\"name\":\"2\",\"content\":\"88\"},\"old\":{\"content\":\"2\"}}";
        Map<String, Object> map = JsonUtils.JsonStringToMap(text);
        System.out.println(map.get("table"));
        Map dataJson = (Map) map.get("data");
        System.out.println(dataJson.get("id"));

//        System.out.println(JsonStringToHashSet("[\"dev:test:sys_order:item:2\",\"dev:test:sys_order:custom:code3333333333:0\",\"dev:test:sys_order:list\",\"dev:test:sys_order:custom:\\\\u6d77\\\\u98de\\\\u4e1d\\\\u6d17\\\\u53d1\\\\u6c34:0\"]"));
        //["dev:test:sys_order:item:2","dev:test:sys_order:custom:code3333333333:0","dev:test:sys_order:list","dev:test:sys_order:custom:\\u6d77\\u98de\\u4e1d\\u6d17\\u53d1\\u6c34:0"]

//         String bb = "[{\"key\":\"dev:test:sys_order:custom:\\\\u8033\\\\u673a:0\",\"dbDatabase\":\"test\",\"dbTable\":\"sys_order\",\"gmtCreate\":1560243670863},{\"key\":\"dev:test:sys_order:custom:code33:0\",\"dbDatabase\":\"test\",\"dbTable\":\"sys_order\",\"gmtCreate\":1560243670863},{\"key\":\"dev:test:sys_order:item:3\",\"dbDatabase\":\"test\",\"dbTable\":\"sys_order\",\"gmtCreate\":1560243670862},{\"key\":\"dev:test:sys_order:list\",\"dbDatabase\":\"test\",\"dbTable\":\"sys_order\",\"gmtCreate\":1560243670862}]";
//         List<RedisExpireAndLoadDTO> redisExpireDTOS = mapper.readValue(bb, new TypeReference<List<RedisExpireAndLoadDTO>>() {
//         });
//         System.out.println(redisExpireDTOS);
//         for (RedisExpireAndLoadDTO d : redisExpireDTOS) {
//             System.out.println(d);
//         }
//         List<String> stringList = redisExpireDTOS.stream().map(RedisExpireAndLoadDTO::getKey).collect(Collectors.toList());
//         System.out.println(stringList);

        String cc = "{\"keyList\":[\"dev:test:sys_order:item:3\",\"dev:test:sys_order:list\",\"dev:test:sys_order:custom:code33:0\",\"dev:test:sys_order:custom:\\\\u8033\\\\u673a:0\"],\"dbDatabase\":\"test\",\"dbTable\":\"sys_order\"}";

//         RedisExpireAndLoadDTO redisExpireDTO = (RedisExpireAndLoadDTO) JsonUtils.JsonStringToObject(cc);
//         System.out.println(redisExpireDTO);


        RedisExpireAndLoadDTO redisExpireDTO =
                JsonUtils.getMapper().readValue(cc, new TypeReference<RedisExpireAndLoadDTO>() {
                });
        System.out.println(redisExpireDTO);


        String k = "{\"minIdle\":5,\"maxPoolSize\":15,\"isAutoCommit\":true,\"idleTimeout\":30000,\"maxLifetime\":1800000,\"connectionTimeout\":30000,\"connectionTestQuery\":\"SELECT 1\"}";
        HikariCpConfig hikariCpConfig = mapper.readValue(k, HikariCpConfig.class);
        System.out.println(hikariCpConfig.toString());



    }
}
