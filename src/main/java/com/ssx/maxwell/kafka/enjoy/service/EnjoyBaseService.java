package com.ssx.maxwell.kafka.enjoy.service;

import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/6 18:27
 * @description: no
 */
public interface EnjoyBaseService<T> extends IService<T> {
    /**
     * 功能描述: 查询所有
     * @param: []
     * @return: java.util.List<T>
     * @author: shuaishuai.xiao
     * @date: 2019/6/6 18:27
     */
    List<T> queryList();

    /**
     * 功能描述: 批量新增or修改
     * @param: [list]
     * @return: java.lang.Integer
     * @author: shuaishuai.xiao
     * @date: 2019/6/6 18:28
     */
    Integer insertOrUpdateBatch(List<T> list);

}
