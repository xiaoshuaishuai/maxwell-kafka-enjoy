package com.ssx.maxwell.kafka.enjoy.controller;

import com.ssx.maxwell.kafka.enjoy.common.model.bo.ElasticsearchMappingBO;
import com.ssx.maxwell.kafka.enjoy.common.model.db.ElasticsearchMappingDO;
import com.ssx.maxwell.kafka.enjoy.common.model.vo.ElasticsearchMappingVO;
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
public class ElasticsearchMappingController extends BaseController<ElasticsearchMappingDO, ElasticsearchMappingVO, ElasticsearchMappingBO> {

    @Autowired
    private ElasticsearchMappingService elasticSearchMappingService;

    @ApiOperation(value = "查询列表", notes = "查询列表", httpMethod = "GET", tags = "1.0.0")
    @RequestMapping("/list")
    @ResponseBody
    @Override
    public List<ElasticsearchMappingVO> list() {
        return elasticSearchMappingService.listToV();
    }


    @ApiOperation(value = "批量插入或修改", notes = "批量插入或修改", httpMethod = "POST", tags = "1.0.0")
    @PostMapping(value = "/insertOrUpdateBatch")
    @Override
    public Integer insertOrUpdateBatch(@RequestBody List<ElasticsearchMappingBO> list) {
        list.forEach(t -> {
            t.setGmtCreate(new Date());
            t.setGmtModify(new Date());
        });
        return elasticSearchMappingService.insertOrUpdateBatch(list);
    }

}
