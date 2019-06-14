package com.ssx.maxwell.kafka.enjoy.controller;

import com.ssx.maxwell.kafka.enjoy.common.model.dto.CacheListDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/14 16:12
 * @description:为其他服务提供获取redis缓存
 */
@RestController
@RequestMapping("/redisCacheList")
@Api(value = "redis映射配置Controller")
public class RedisCacheListController {

    @ApiOperation(value = "查询redis缓存", notes = "查询redis缓存", httpMethod = "GET", tags = "1.0.0")
    @RequestMapping("/get")
    @ResponseBody
    public CacheListDTO get(@RequestParam(value = "key") String key) {
        //todo 2019-6-14 16:15:59 使用key查询redis
        //如果缓存不存在、解析key 查询db


        return null;
    }
}
