package com.ssx.maxwell.kafka.enjoy.common.tools;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.google.common.base.Strings;
import com.ssx.maxwell.kafka.enjoy.common.model.dto.RedisExpireAndLoadDTO;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/4 14:58
 * @description:
 */
@Slf4j
public final class JsonUtils {
    @Getter
    private static final ObjectMapper mapper = new ObjectMapper();
    private static Pattern linePattern = Pattern.compile("_(\\w)");
    private static Pattern humpPattern = Pattern.compile("[A-Z]");

    /**
     * 对象转string
     * @param object
     * @return
     * @throws JsonProcessingException
     */
    public static final String ObjectToJsonString(Object object) throws JsonProcessingException {
        if(null == object){
            return null;
        }
        return mapper.writeValueAsString(object);
    }

    public static final Map<String, Object> JsonStringToMap(String text) throws IOException {
        if(Strings.isNullOrEmpty(text)){
            return null;
        }
        return mapper.readValue(text, Map.class);
    }

    public static final HashSet<Object> JsonStringToHashSet(String text) throws IOException {
        if(Strings.isNullOrEmpty(text)){
            return null;
        }
        return mapper.readValue(text, HashSet.class);
    }

    public static final Object JsonStringToObject(String text) throws IOException {
        if(Strings.isNullOrEmpty(text)){
            return null;
        }
        return mapper.readValue(text, Object.class);
    }

    /**
     * 将对象的大写转换为下划线加小写，例如：userName-->user_name
     *
     * @param object
     * @return
     * @throws JsonProcessingException
     */
    public static String toUnderlineJSONString(Object object) throws JsonProcessingException {
        if(null == object){
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String reqJson = mapper.writeValueAsString(object);
        return reqJson;
    }

    /**
     * 将下划线转换为驼峰的形式，例如：user_name-->userName
     *
     * @param json
     * @param clazz
     * @return
     * @throws IOException
     */
    public static <T> T toSnakeObject(String json, Class<T> clazz) throws IOException {
        if(Strings.isNullOrEmpty(json)){
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        T reqJson = mapper.readValue(json, clazz);
        return reqJson;
    }



    /** 下划线转驼峰 */
    public static String lineToHump(String str) {
        if(Strings.isNullOrEmpty(str)){
            return null;
        }
        str = str.toLowerCase();
        Matcher matcher = linePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /** 驼峰转下划线,效率比上面高 */
    public static String humpToLine2(String str) {
        if(Strings.isNullOrEmpty(str)){
            return null;
        }
        Matcher matcher = humpPattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static void main(String[] args) throws IOException {

        String text = "{\"database\":\"test\",\"table\":\"title\",\"type\":\"update\",\"ts\":1559542729,\"xid\":2470,\"commit\":true,\"data\":{\"id\":2,\"name\":\"2\",\"content\":\"88\"},\"old\":{\"content\":\"2\"}}";
        Map<String, Object> map = JsonUtils.JsonStringToMap(text);
        System.out.println(map.get("table"));
        Map dataJson = (Map) map.get("data");
        System.out.println(dataJson.get("id"));

        System.out.println(JsonStringToHashSet("[\"dev:test:sys_order:item:2\",\"dev:test:sys_order:custom:code3333333333:0\",\"dev:test:sys_order:list\",\"dev:test:sys_order:custom:\\\\u6d77\\\\u98de\\\\u4e1d\\\\u6d17\\\\u53d1\\\\u6c34:0\"]"));
        //["dev:test:sys_order:item:2","dev:test:sys_order:custom:code3333333333:0","dev:test:sys_order:list","dev:test:sys_order:custom:\\u6d77\\u98de\\u4e1d\\u6d17\\u53d1\\u6c34:0"]

//        String bb = "[{\"key\":\"dev:test:sys_order:custom:\\\\u8033\\\\u673a:0\",\"dbDatabase\":\"test\",\"dbTable\":\"sys_order\",\"gmtCreate\":1560243670863},{\"key\":\"dev:test:sys_order:custom:code33:0\",\"dbDatabase\":\"test\",\"dbTable\":\"sys_order\",\"gmtCreate\":1560243670863},{\"key\":\"dev:test:sys_order:item:3\",\"dbDatabase\":\"test\",\"dbTable\":\"sys_order\",\"gmtCreate\":1560243670862},{\"key\":\"dev:test:sys_order:list\",\"dbDatabase\":\"test\",\"dbTable\":\"sys_order\",\"gmtCreate\":1560243670862}]";
//        List<RedisExpireAndLoadDTO> redisExpireDTOS = mapper.readValue(bb, new TypeReference<List<RedisExpireAndLoadDTO>>() {
//        });
//        System.out.println(redisExpireDTOS);
//        for (RedisExpireAndLoadDTO d : redisExpireDTOS) {
//            System.out.println(d);
//        }
//        List<String> stringList = redisExpireDTOS.stream().map(RedisExpireAndLoadDTO::getKey).collect(Collectors.toList());
//        System.out.println(stringList);

        String cc = "{\"keyList\":[\"dev:test:sys_order:item:3\",\"dev:test:sys_order:list\",\"dev:test:sys_order:custom:code33:0\",\"dev:test:sys_order:custom:\\\\u8033\\\\u673a:0\"],\"dbDatabase\":\"test\",\"dbTable\":\"sys_order\"}";

//        RedisExpireAndLoadDTO redisExpireDTO = (RedisExpireAndLoadDTO) JsonUtils.JsonStringToObject(cc);
//        System.out.println(redisExpireDTO);


        RedisExpireAndLoadDTO redisExpireDTO =
                JsonUtils.getMapper().readValue(cc, new TypeReference<RedisExpireAndLoadDTO>() {
                });
        System.out.println(redisExpireDTO);


        System.out.println(lineToHump("master"));
        System.out.println(lineToHump("business_test"));

    }
}
