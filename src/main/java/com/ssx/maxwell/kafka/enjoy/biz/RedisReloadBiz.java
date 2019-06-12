package com.ssx.maxwell.kafka.enjoy.biz;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/12 13:44
 * @description: 加载缓存至redis
 */
public interface RedisReloadBiz {
    boolean reloadCache(String dbDatabase, String dbTable, Long dbPid);
}
