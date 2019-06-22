package com.ssx.maxwell.kafka.enjoy.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.ssx.maxwell.kafka.enjoy.common.model.bo.test.SysOrderBO;
import com.ssx.maxwell.kafka.enjoy.common.model.db.test.SysOrderDO;
import com.ssx.maxwell.kafka.enjoy.common.model.vo.test.SysOrderVO;
import com.ssx.maxwell.kafka.enjoy.mapper.SysOrderMapper;
import com.ssx.maxwell.kafka.enjoy.service.SysOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * 服务实现类
 *
 * @author shuaishuai.xiao
 * @create 2019-06-11
 */
@Service
@DS("business_test")
public class SysOrderServiceImpl extends EnjoyBaseServiceImpl<SysOrderDO, SysOrderVO, SysOrderBO, SysOrderMapper> implements SysOrderService {
    @Autowired
    private SysOrderMapper sysOrderMapper;

    @PostConstruct
    public void init() {
        super.mapper = sysOrderMapper;
    }

    @Override
    public SysOrderVO newVO() {
        return new SysOrderVO();
    }

    @Override
    public SysOrderDO newDO() {
        return new SysOrderDO();
    }
}
