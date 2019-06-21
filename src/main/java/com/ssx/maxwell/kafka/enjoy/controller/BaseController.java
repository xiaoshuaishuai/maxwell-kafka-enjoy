package com.ssx.maxwell.kafka.enjoy.controller;

import java.util.List;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/11 11:13
 * @description: no
 */
public abstract class BaseController<D, V, B> {

    public abstract List<V> list();

    public abstract Integer insertOrUpdateBatch(List<B> list);
}
