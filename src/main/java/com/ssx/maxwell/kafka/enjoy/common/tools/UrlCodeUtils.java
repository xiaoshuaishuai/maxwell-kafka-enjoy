package com.ssx.maxwell.kafka.enjoy.common.tools;

import com.google.common.base.Strings;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/7/8 10:54
 * @description: url编解码
 */
public class UrlCodeUtils {
    public static final Charset UTF_8 = Charset.forName("UTF-8");

    /**
     * 功能描述: url编码
     * @param: [text]
     * @return: java.lang.String
     * @author: shuaishuai.xiao
     * @date: 2019/7/8 11:00
     */
    public static String encode(String text) throws UnsupportedEncodingException {
        return URLEncoder.encode(text, UTF_8.name());
    }
    /**
     * 功能描述: url解码
     * @param: [encodeText]
     * @return: java.lang.String
     * @author: shuaishuai.xiao
     * @date: 2019/7/8 11:00
     */
    public static String decode(String encodeText) throws UnsupportedEncodingException {
        return URLDecoder.decode(encodeText, UTF_8.name());
    }
    /**
     * 功能描述: 是
     * @param: [string]
     * @return: boolean
     * @author: shuaishuai.xiao
     * @date: 2019/7/8 11:07
     */
    public static boolean isEncode(String string){
        if(Strings.isNullOrEmpty(string)){
            return false;
        }
        return  string.startsWith("%") && !string.endsWith("%");
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        String t = UrlCodeUtils.encode("哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈或或或或或或或或或或或或或或或或或或或或或或或或或或hhhhhhhhhhhh");
        System.out.println(t );
        System.out.println(UrlCodeUtils.decode(t ));
    }
}
