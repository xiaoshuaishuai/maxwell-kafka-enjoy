package com.ssx.maxwell.kafka.enjoy.common.tools;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ssx.maxwell.kafka.enjoy.enumerate.MaxwellBinlogConstants;
import org.apache.commons.lang3.ArrayUtils;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/7/5 16:40
 * @description:
 */
public class TemplateUtils {

    /**
     * 配置模板转自定义缓存key的后缀
     * :order_code:is_deleted(1800) 转 :2:0
     *
     * @param template
     * @param dataJson
     * @param oldDataJson
     * @return
     */
    public static List<String> templateConversionKey(String template, Map dataJson, Map oldDataJson) throws UnsupportedEncodingException {
        List<String> resList = Lists.newArrayList();
        if (!Strings.isNullOrEmpty(template)) {
            String[] templateArr = template.split(",");
            if (ArrayUtils.isNotEmpty(templateArr)) {
                for (String templateString : templateArr) {
                    if(!Strings.isNullOrEmpty(templateConversionKeyAlone(templateString, dataJson, oldDataJson))){
                        resList.add(templateConversionKeyAlone(templateString, dataJson, oldDataJson));
                    }
                }
            }
        }
        return resList;
    }
    /**
     * 功能描述: 用于构建删除key
     * @param: [template, dataJson, oldDataJson]
     * @return: java.lang.String
     * @author: shuaishuai.xiao
     * @date: 2019/7/8 15:01
     */
    public static String templateConversionKeyAlone(String template, Map dataJson, Map oldDataJson) throws UnsupportedEncodingException {
        StringBuilder columnStringBuilder = new StringBuilder();
        if (!Strings.isNullOrEmpty(template) && template.contains(":")) {
            String[] columnArr = template.split(":");
            if (ArrayUtils.isNotEmpty(columnArr)) {
                for (String columnString : columnArr) {
                    if (columnString.contains("(")) {
                        //筛选掉过期时间配置
                        columnString = columnString.substring(0, columnString.indexOf("("));
                    }
                    if(!Strings.isNullOrEmpty(columnString)){
                        if (null != oldDataJson && oldDataJson.containsKey(columnString)) {
                            getJsonData(oldDataJson, columnString, columnStringBuilder);
                        }else if (null != dataJson && dataJson.containsKey(columnString)) {
                            getJsonData(dataJson, columnString, columnStringBuilder);
                        }
                    }
                }
            }
        }
        return columnStringBuilder.toString();
    }
    /**
     * 功能描述: 用于构建重载key
     * @param: [template, dataJson]
     * @return: java.lang.String
     * @author: shuaishuai.xiao
     * @date: 2019/7/8 15:02
     */
    public static String templateConversionKeyAlone(String template, Map dataJson) throws UnsupportedEncodingException {
        StringBuilder columnStringBuilder = new StringBuilder();
        if (!Strings.isNullOrEmpty(template) && template.contains(":")) {
            String[] columnArr = template.split(":");
            if (ArrayUtils.isNotEmpty(columnArr)) {
                for (String columnString : columnArr) {
                    if (columnString.contains("(")) {
                        //筛选掉过期时间配置
                        columnString = columnString.substring(0, columnString.indexOf("("));
                    }
                    if(!Strings.isNullOrEmpty(columnString)){
                        if (null != dataJson && dataJson.containsKey(columnString)) {
                            getJsonData(dataJson, columnString, columnStringBuilder);
                        }
                    }
                }
            }
        }
        return columnStringBuilder.toString();
    }

    private static void getJsonData(Map dataJson, String columnString, StringBuilder columnStringBuilder) throws UnsupportedEncodingException {
        columnStringBuilder.append(":");
        if (dataJson.get(columnString) instanceof String) {
            //字符串判断是否包含中文
            if (PatternUtils.isContainChinese((String) dataJson.get(columnString))) {
                //转码
                columnStringBuilder.append(UrlCodeUtils.encode((String) dataJson.get(columnString)));
            } else {
                //字符串类型参数为空NONE填充
                columnStringBuilder.append(Strings.isNullOrEmpty(String.valueOf(dataJson.get(columnString))) ? MaxwellBinlogConstants.REDIS_VAL_NONE_MAGIC : dataJson.get(columnString));
            }
        } else {
            columnStringBuilder.append(null == dataJson.get(columnString) ? MaxwellBinlogConstants.REDIS_VAL_NONE_MAGIC : dataJson.get(columnString));
        }
    }

    /**
     * 中文URL编码、 因为Unicode编码之后包含\ \ \ ,redis 模糊查询过滤不出， 所以此处采用了URL编码
     * 值为空转NONE填充
     * @param dbObj
     * @param keyBuilder
     */
    public static void encodeRedisKeySuffix(Object dbObj, StringBuilder keyBuilder) throws UnsupportedEncodingException {
        if (null == dbObj || "".equals(dbObj)) {
            keyBuilder.append(MaxwellBinlogConstants.REDIS_VAL_NONE_MAGIC);
        } else {
            if (dbObj instanceof String) {
                //字符串判断是否包含中文
                if (PatternUtils.isContainChinese((String) dbObj)) {
                    //url编码
                    keyBuilder.append(UrlCodeUtils.encode((String) dbObj));
                } else {
                    keyBuilder.append(dbObj);
                }
            } else {
                keyBuilder.append(dbObj);
            }
        }
    }
}
