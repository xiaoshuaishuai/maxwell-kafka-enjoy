package com.ssx.maxwell.kafka.enjoy.biz;

import com.ssx.maxwell.kafka.enjoy.common.model.RespData;
import com.ssx.maxwell.kafka.enjoy.common.model.dto.RedisCacheListDTO;
import com.ssx.maxwell.kafka.enjoy.enumerate.GlobalCallbackEnum;
import lombok.NonNull;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/7/1 14:11
 * @description:
 */
public interface RedisCacheListGetAndLoadBiz {
    /**
     * 直接通过redis key查询缓存，如果缓存有直接返回，如果缓存没有，先查询redis_mapping是否配置了该表缓存模板
     * 如果配置了加载数据库数据至缓存 并返回
     * 如果redis_mapping没有配置，响应客户端code码 {@link GlobalCallbackEnum.REDIS_MAPPING_NO_DEFIEND}
     * <p>
     * 针对于客户端 通过key 直接查询场景
     *
     * @param key
     * @return
     */
    RespData<RedisCacheListDTO> get(@NonNull String key);
//    /**
//     * 全表缓存查询
//     * @param dbDatabase
//     * @param dbTable
//     * @param dbPid
//     * @return
//     */
//    RespData<RedisCacheListDTO> listTable(String dbDatabase, String dbTable, String dbPid);
//    /**
//     * 主键id缓存查询
//     * @param dbDatabase
//     * @param dbTable
//     * @param dbPid
//     * @return
//     */
//    RespData<RedisCacheListDTO> getByPrimaryId(String dbDatabase, String dbTable, String dbPid);

}
