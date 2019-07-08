package com.ssx.maxwell.kafka.enjoy.common.model.dto;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/11 15:33
 * @description: no
 */
@Data
@EqualsAndHashCode
public class RedisExpireAndLoadDTO {
    /**
     * 删除key集合
     */
    private List<String> deleteKeyList = Lists.newArrayList();

    /**
     * 数据库
     */
    private String dbDatabase;
    /**
     * 表
     */
    private String dbTable;
    /**
     * 目标表的主键id
     */
    private String dbPid;

    /**
     * update情况下
     * 修改的原始字段和值
     * 用于清理自定义缓存相关字段修改之前旧的缓存
     */
    private Map oldDataJson;
    /**
     * 最新data
     */
    private Map dataJson;

//    private List<ReloadKeyDTO> reloadKeyDTOS  = Lists.newArrayList();
//
//    @Data
//    @Accessors(chain = true)
//    public static class ReloadKeyDTO{
//        /**
//         * 删除的缓存对应的模板，模板对应的缓存需要重新装载
//         */
//        private String templates;
//        /**
//         * 模糊key
//         */
//        private String fuzzyKey;
//    }
}
