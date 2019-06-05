package com.ssx.maxwell.kafka.enjoy.common.tools;

import java.util.regex.Pattern;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/4 18:15
 * @description:
 */
public class PatternUtils {
    /**
     * 功能描述: 判断是否包含中文
     *
     * @param: [str]
     * @return: boolean
     * @author: shuaishuai.xiao
     * @date: 2019/6/4 18:15
     */
    public static boolean isContainChinese(String str) {
        return Pattern.compile("[\u4e00-\u9fa5]").matcher(str).find();
    }
}
