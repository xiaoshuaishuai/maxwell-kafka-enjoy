package com.ssx.maxwell.kafka.enjoy.common.tools;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ssx.maxwell.kafka.enjoy.enumerate.MaxwellBinlogConstants;
import org.apache.commons.lang3.ArrayUtils;

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
     * @return
     */
    public static List<String> templateConversionKey(String template, Map dataJson) {
        List<String> resList = Lists.newArrayList();
        if (!Strings.isNullOrEmpty(template)) {
            String[] templateArr = template.split(",");
            if (ArrayUtils.isNotEmpty(templateArr)) {
                for (String templateString : templateArr) {
                    if(!Strings.isNullOrEmpty(templateConversionKeyAlone(templateString, dataJson))){
                        resList.add(templateConversionKeyAlone(templateString, dataJson));
                    }
                }
            }
        }
        return resList;
    }

    public static String templateConversionKeyAlone(String template, Map dataJson) {
        StringBuilder columnStringBuilder = new StringBuilder();
        if (!Strings.isNullOrEmpty(template) && template.contains(":")) {
            String[] columnArr = template.split(":");
            if (ArrayUtils.isNotEmpty(columnArr)) {
                for (String columnString : columnArr) {
                    if (columnString.contains("(")) {
                        //筛选掉过期时间配置
                        columnString = columnString.substring(0, columnString.indexOf("("));
                    }
                    if (dataJson.containsKey(columnString)) {
                        columnStringBuilder.append(":");
                        if (dataJson.get(columnString) instanceof String) {
                            //字符串判断是否包含中文
                            if (PatternUtils.isContainChinese((String) dataJson.get(columnString))) {
                                //转码
                                columnStringBuilder.append(UnicodeUtils.cnToUnicode((String) dataJson.get(columnString)));
                            } else {
                                //字符串类型参数为空NONE填充
                                columnStringBuilder.append(Strings.isNullOrEmpty(String.valueOf(dataJson.get(columnString))) ? MaxwellBinlogConstants.REDIS_VAL_NONE_MAGIC : dataJson.get(columnString));
                            }
                        } else {
                            columnStringBuilder.append(null == dataJson.get(columnString) ? MaxwellBinlogConstants.REDIS_VAL_NONE_MAGIC : dataJson.get(columnString));
                        }
                    }
                }
            }
        }
        return columnStringBuilder.toString();
    }

    /**
     * 中文转Unicode
     * 值为空转NONE填充
     * @param dbObj
     * @param keyBuilder
     */
    public static void encodeRedisKeySuffix(Object dbObj, StringBuilder keyBuilder){
        if (null == dbObj || "".equals(dbObj)) {
            keyBuilder.append(MaxwellBinlogConstants.REDIS_VAL_NONE_MAGIC);
        } else {
            if (dbObj instanceof String) {
                //字符串判断是否包含中文
                if (PatternUtils.isContainChinese((String) dbObj)) {
                    //转unicode码
                    keyBuilder.append(UnicodeUtils.cnToUnicode((String) dbObj));
                } else {
                    keyBuilder.append(dbObj);
                }
            } else {
                keyBuilder.append(dbObj);
            }
        }
    }
}
