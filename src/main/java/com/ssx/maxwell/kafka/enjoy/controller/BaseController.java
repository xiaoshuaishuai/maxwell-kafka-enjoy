package com.ssx.maxwell.kafka.enjoy.controller;

import com.ssx.maxwell.kafka.enjoy.common.model.RespData;
import com.ssx.maxwell.kafka.enjoy.enumerate.GlobalCallbackEnum;

import java.util.List;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/11 11:13
 * @description: no
 */
public abstract class BaseController<D, V, B> {

    public abstract RespData<List<V>> list();

    public abstract RespData<Integer> insertOrUpdateBatch(List<B> list);

    public RespData<Void> success() {
        return callback(GlobalCallbackEnum.SUCCESS);
    }

    public <T> RespData<T> success(T data) {
        return callback(GlobalCallbackEnum.SUCCESS, data);
    }

    public RespData<Void> failure() {
        return callback(GlobalCallbackEnum.SERVICE_ERROR);
    }

    public <T> RespData<T> failure(T data) {
        return callback(GlobalCallbackEnum.SERVICE_ERROR, data);
    }

    /**
     * 公共响应,无返回值
     */
    public static <T> RespData<T> callback(GlobalCallbackEnum globalCallbackEnum) {
        return callback(globalCallbackEnum, null);
    }

    /**
     * 公共响应,有返回值
     */
    public static <T> RespData<T> callback(GlobalCallbackEnum globalCallbackEnum, T data) {
        return callback(globalCallbackEnum.getValue(), globalCallbackEnum.getIntro(), data);
    }

    /**
     * 公共响应,有返回值
     */
    public static <T> RespData<T> callback(Integer code, String message, T data) {
        return new RespData<T>(code, message, data);
    }

}
