package com.ssx.maxwell.kafka.enjoy.biz;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/12 13:44
 * @description: 加载缓存至redis
 */
public interface RedisReloadBiz {
    /**
     * 功能描述: 加载缓存至redis
     * @param: [dbDatabase, dbTable, dbPid]
     * @return: boolean
     * @author: shuaishuai.xiao
     * @date: 2019/6/14 13:50
     */
    boolean reloadCache(String dbDatabase, String dbTable, Long dbPid);
}
