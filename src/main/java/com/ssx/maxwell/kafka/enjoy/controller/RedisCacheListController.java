package com.ssx.maxwell.kafka.enjoy.controller;

import com.ssx.maxwell.kafka.enjoy.biz.RedisCacheListGetAndLoadBiz;
import com.ssx.maxwell.kafka.enjoy.common.model.RespData;
import com.ssx.maxwell.kafka.enjoy.common.model.dto.RedisCacheListDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private RedisCacheListGetAndLoadBiz redisCacheListGetAndLoadBiz;

    /**
     *
     * @param key 如果:: key中间包含中文需要URL编码，否则中文key查询匹配不上
     * @param template 自定义key如果缓存没有需要从数据加载需要给出redis_mapping表配置的key对应的模板
     *                 举例： key=dev:test:sys_order:3:custom:code6:0  template=:order_code:is_deleted(1800)
     *                 返回值为string格式的json字符串，需要转RedisCacheListDTO，由客户端决定具体的对象。
     *                 举例：{"obj":[{"deleted":0,"gmtModify":1562576228000,"orderCode":"code6","id":6,"category":0,"gmtCreate":1560505163000,"goodsName":"梳子","sendExpress":0}],"none":false}
     *                 Java转换：
     *                 RedisCacheListDTO<SysOrderDO> redisCacheListDTO =
     *                        JsonUtils.getMapper().readValue(v, new TypeReference<RedisCacheListDTO<SysOrderDO>>() {
     *                 });
     * @return
     */
    @ApiOperation(value = "查询redis缓存返回JSON", notes = "查询redis缓存返回JSON", httpMethod = "GET", tags = "1.0.0")
    @RequestMapping("/get")
    @ResponseBody
    public RespData<String> get(@RequestParam(value = "key") String key, @RequestParam(value = "template", required = false) String template) {
        return redisCacheListGetAndLoadBiz.get(key, template);
    }

    /**
     * 直接转换为对象 RedisCacheListDTO
     * @param key
     * @param template
     * @return
     */
    @ApiOperation(value = "查询redis缓存返回对象", notes = "查询redis缓存返回对象", httpMethod = "GET", tags = "1.0.0")
    @RequestMapping("/getObj")
    @ResponseBody
    public RespData<RedisCacheListDTO> getObj(@RequestParam(value = "key") String key, @RequestParam(value = "template", required = false) String template) {
        return redisCacheListGetAndLoadBiz.getObj(key, template);
    }
}
