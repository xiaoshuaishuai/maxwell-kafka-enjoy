package com.ssx.maxwell.kafka;

import com.ssx.maxwell.kafka.enjoy.enumerate.KafkaGroupEnum;

import java.util.Arrays;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/3 17:48
 * @description:
 */
public class Test {
    public static void main(String[] args) {

        String s = ":order_code:is_del,:goods_name:is_del";
        String s2 = "1|2|3";
        String s3 = "1,2,3";
        System.out.println(Arrays.toString(s.split(",")));
        System.out.println(Arrays.toString(s2.split("|")));
        System.out.println(Arrays.toString(s3.split(",")));
    }
}
