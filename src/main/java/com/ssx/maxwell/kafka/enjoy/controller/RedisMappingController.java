package com.ssx.maxwell.kafka.enjoy.controller;

import com.ssx.maxwell.kafka.enjoy.common.model.RespData;
import com.ssx.maxwell.kafka.enjoy.common.model.bo.RedisMappingBO;
import com.ssx.maxwell.kafka.enjoy.common.model.datao.RedisMappingDO;
import com.ssx.maxwell.kafka.enjoy.common.model.vo.RedisMappingVO;
import com.ssx.maxwell.kafka.enjoy.service.RedisMappingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/6 10:20
 * @description:
 */
@RestController
@RequestMapping("/redisMapping")
@Api(value = "redis映射配置Controller")
public class RedisMappingController extends BaseController<RedisMappingDO, RedisMappingVO, RedisMappingBO> {

    @Autowired
    private RedisMappingService redisMappingService;

    @ApiOperation(value = "查询列表", notes = "查询列表", httpMethod = "GET", tags = "1.0.0")
    @RequestMapping("/list")
    @ResponseBody
    @Override
    public RespData<List<RedisMappingVO>> list() {
        return success(redisMappingService.listToV());
    }

    @ApiOperation(value = "批量插入或修改", notes = "批量插入或修改", httpMethod = "POST", tags = "1.0.0")
    @PostMapping(value = "/insertOrUpdateBatch")
    @Override
    public RespData<Integer> insertOrUpdateBatch(@RequestBody List<RedisMappingBO> list) {
        list.forEach(t -> {
            t.setGmtCreate(new Date());
            t.setGmtModify(new Date());
        });
        return success(redisMappingService.insertOrUpdateBatch(list));
    }

}
