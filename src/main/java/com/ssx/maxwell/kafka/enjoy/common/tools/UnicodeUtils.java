package com.ssx.maxwell.kafka.enjoy.common.tools;

import com.google.common.base.Strings;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/4 18:20
 * @description:
 */
public class UnicodeUtils {

    public static void main(String[] args) {
        String name = "马甲";
        System.out.println(cnToUnicode(name));
        System.out.println(unicodeToCn(cnToUnicode(name)));
        System.out.println(isUnicode(cnToUnicode(name)));
        System.out.println(isUnicode(unicodeToCn(cnToUnicode(name))));
    }

    //unicode转成中文
    public static String unicodeToCn(String unicode) {
        /** 以 \ u 分割，因为java注释也能识别unicode，因此中间加了一个空格*/
        String[] strs = unicode.split("\\\\u");
        String returnStr = "";
        //  由于unicode字符串以 \ u 开头，因此分割出的第一个字符是""。
        for (int i = 1; i < strs.length; i++) {
            returnStr += (char) Integer.valueOf(strs[i], 16).intValue();
        }
        return returnStr;
    }

    //中文转成unicode
    public static String cnToUnicode(String cn) {
        char[] chars = cn.toCharArray();
        String returnStr = "";
        for (int i = 0; i < chars.length; i++) {
            returnStr += "\\u" + Integer.toString(chars[i], 16);
        }
        return returnStr;
    }

    /**
     * 判断是否为Unicode编码格式
     * @param string
     * @return
     */
    public static boolean isUnicode(String string){
        if(Strings.isNullOrEmpty(string)){
            return false;
        }
        return string.contains("\\u");
    }
}
