package com.ssx.maxwell.kafka.enjoy;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.ssx.maxwell.kafka.enjoy.common.model.datao.SysOrderDO;
import com.ssx.maxwell.kafka.enjoy.common.model.dto.RedisCacheListDTO;
import com.ssx.maxwell.kafka.enjoy.common.tools.ClassUtils;
import com.ssx.maxwell.kafka.enjoy.common.tools.JsonUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/7/8 17:33
 * @description:
 */
public class Test {
    public static void main(String[] args) {
        System.out.println(SysOrderDO.class.getName());
        System.out.println(SysOrderDO.class.getSimpleName());

        List<Class<?>> classList =  ClassUtils.getClasses("com.ssx.maxwell.kafka.enjoy.common.model.datao");

//        String json = "{\"obj\":[{\"deleted\":0,\"gmtModify\":1562576228000,\"orderCode\":\"code6\",\"id\":6,\"category\":0,\"gmtCreate\":1560505163000,\"goodsName\":\"梳子\",\"sendExpress\":0}],\"none\":false}";
        String json = "{\"obj\":[{\"deleted\":0,\"gmtModify\":1562576228000,\"orderCode\":\"code6\",\"id\":6,\"category\":0,\"gmtCreate\":1560505163000,\"goodsName\":\"梳子\",\"sendExpress\":0},{\"deleted\":0,\"gmtModify\":1562570194000,\"orderCode\":\"code7\",\"id\":7,\"category\":0,\"gmtCreate\":1562554131000,\"goodsName\":\"brother\",\"sendExpress\":0},{\"deleted\":0,\"gmtModify\":1562564963000,\"orderCode\":\"code1\",\"id\":1,\"category\":0,\"gmtCreate\":1559640105000,\"goodsName\":\"牙膏\",\"sendExpress\":0},{\"deleted\":0,\"gmtModify\":1560506220000,\"orderCode\":\"code3\",\"id\":3,\"category\":0,\"gmtCreate\":1560243666000,\"goodsName\":\"耳机\",\"sendExpress\":0},{\"deleted\":0,\"gmtModify\":1560499770000,\"orderCode\":\"code2\",\"id\":2,\"category\":0,\"gmtCreate\":1559640105000,\"goodsName\":\"海飞丝洗发水\",\"sendExpress\":0},{\"deleted\":0,\"gmtModify\":1560495702000,\"orderCode\":\"code5\",\"id\":5,\"category\":0,\"gmtCreate\":1560495702000,\"goodsName\":\"法海\",\"sendExpress\":0},{\"deleted\":0,\"gmtModify\":1560495664000,\"orderCode\":\"code4\",\"id\":4,\"category\":0,\"gmtCreate\":1560495664000,\"goodsName\":\"马甲\",\"sendExpress\":0}],\"none\":false}";

        classList.forEach(cls->{

            if("SysOrderDO".equals(cls.getSimpleName())){
                JavaType userType = TypeFactory.defaultInstance().constructParametricType(RedisCacheListDTO.class, cls);
                try {
                    RedisCacheListDTO redisCacheListDTO = JsonUtils.getMapper().readValue(json, userType);
                    List l = redisCacheListDTO.getObj();
                    l.forEach(tt-> System.out.println(tt.toString()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

//            System.out.println(cls.getSimpleName());
        });

//            RedisCacheListDTO<> redisCacheListDTO =
//                    JsonUtils.getMapper().readValue(json, new TypeReference<RedisCacheListDTO<o>>() {
//                    });



    }

    public void a (){

    }
}
