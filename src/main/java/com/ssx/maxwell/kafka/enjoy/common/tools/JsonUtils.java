package com.ssx.maxwell.kafka.enjoy.common.tools;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.ssx.maxwell.kafka.enjoy.common.model.datao.SysOrderDO;
import com.ssx.maxwell.kafka.enjoy.common.model.dto.RedisCacheListDTO;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;

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

        String v = "{\"obj\":[{\"deleted\":0,\"gmtModify\":1562576228000,\"orderCode\":\"code6\",\"id\":6,\"category\":0,\"gmtCreate\":1560505163000,\"goodsName\":\"梳子\",\"sendExpress\":0}],\"none\":false}";


        RedisCacheListDTO<SysOrderDO> redisCacheListDTO =
                JsonUtils.getMapper().readValue(v, new TypeReference<RedisCacheListDTO<SysOrderDO>>() {
                });

        System.out.println(redisCacheListDTO);
    }
}
