package com.ssx.maxwell.kafka.enjoy.biz;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Map;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/12 13:44
 * @description: 加载缓存至redis
 */
public interface RedisReloadBiz {
    /**
     * 功能描述: 加载缓存至redis
     * @param: [dbDatabase, dbTable, dbPid, reloadKeyDTOS]
     * @return: boolean
     * @author: shuaishuai.xiao
     * @date: 2019/6/14 13:50
     */
    boolean reloadCache(@NonNull String dbDatabase, @NonNull String dbTable, @NonNull String dbPid, @NonNull Map dataJson, @Nullable  Map oldDataJson);
}
