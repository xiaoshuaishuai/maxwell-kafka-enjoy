package com.ssx.maxwell.kafka.enjoy.controller;

import com.ssx.maxwell.kafka.enjoy.common.model.entity.ElasticsearchMappingEntity;
import com.ssx.maxwell.kafka.enjoy.service.ElasticsearchMappingService;
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
@RequestMapping("/elasticsearchMapping")
@Api(value = "elasticSearch映射配置Controller")
public class ElasticsearchMappingController {

    @Autowired
    private ElasticsearchMappingService elasticSearchMappingService;

    @ApiOperation(value = "查询列表", notes = "查询列表", httpMethod = "GET", tags = "1.0.0")
    @RequestMapping("/list")
    @ResponseBody
    public List<ElasticsearchMappingEntity> queryList() {
        return elasticSearchMappingService.queryList();
    }

    @ApiOperation(value = "批量插入或修改", notes = "批量插入或修改", httpMethod = "POST", tags = "1.0.0")
    @PostMapping(value = "/insertOrUpdateBatch")
    public Integer insertOrUpdateBatch(@RequestBody List<ElasticsearchMappingEntity> list) {
        list.forEach(t -> {
            t.setGmtCreate(new Date());
            t.setGmtModify(new Date());
        });
        return elasticSearchMappingService.insertOrUpdateBatch(list);
    }

}
