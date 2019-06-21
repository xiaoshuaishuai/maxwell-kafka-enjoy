package com.ssx.maxwell.kafka.enjoy.service;

import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/6 18:27
 * @description: no
 */
public interface EnjoyBaseService<D, V, B> extends IService<D> {
    /**
     * 功能描述: 查询所有
     *
     * @param: []
     * @return: java.util.List<T>
     * @author: shuaishuai.xiao
     * @date: 2019/6/6 18:27
     */
    List<D> list();

    /**
     * 功能描述: 查询所有转视图对象
     *
     * @param: []
     * @return: java.util.List<V>
     * @author: shuaishuai.xiao
     * @date: 2019/6/21 11:35
     */
    List<V> listToV();

    /**
     * 功能描述: new 视图对象
     * @param: []
     * @return: V
     * @author: shuaishuai.xiao
     * @date: 2019/6/21 11:43
     */
    V newVO();

    /**
     * 功能描述: 创建 数据库对象
     * @param: []
     * @return: D
     * @author: shuaishuai.xiao
     * @date: 2019/6/21 11:49
     */
    D newDO();

    /**
     * 功能描述: 批量新增or修改
     *
     * @param: [list]
     * @return: java.lang.Integer
     * @author: shuaishuai.xiao
     * @date: 2019/6/6 18:28
     */
    Integer insertOrUpdateBatch(List<B> list);

}
