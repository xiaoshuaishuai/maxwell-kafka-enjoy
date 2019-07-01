package com.ssx.maxwell.kafka.enjoy.controller;

import com.ssx.maxwell.kafka.enjoy.common.model.RespData;
import com.ssx.maxwell.kafka.enjoy.common.model.bo.test.SysOrderBO;
import com.ssx.maxwell.kafka.enjoy.common.model.db.test.SysOrderDO;
import com.ssx.maxwell.kafka.enjoy.common.model.vo.test.SysOrderVO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 控制器
 * @author shuaishuai.xiao
 * @create 2019-06-11
 */
@RestController
@RequestMapping("/sysOrder")
public class SysOrderController extends BaseController<SysOrderDO, SysOrderVO, SysOrderBO> {

    @Override
    public RespData<List<SysOrderVO>> list() {
        return null;
    }

    @Override
    public RespData<Integer> insertOrUpdateBatch(List<SysOrderBO> list) {
        return null;
    }
}
