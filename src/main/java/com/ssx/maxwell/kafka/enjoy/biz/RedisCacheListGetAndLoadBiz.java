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
     * @param template 针对自定义缓存，需要把模板传过来 :order_code:is_deleted(1800), 其他缓存可传"", 如果template不传，redis没有将不会从数据库查询
     * @return
     */
    RespData<String> get(@NonNull String key, String template);

    RespData<RedisCacheListDTO> getObj(@NonNull String key, String template);

}
