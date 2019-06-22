package com.ssx.maxwell.kafka.enjoy.service;

import com.ssx.maxwell.kafka.enjoy.common.model.bo.RedisMappingBO;
import com.ssx.maxwell.kafka.enjoy.common.model.db.RedisMappingDO;
import com.ssx.maxwell.kafka.enjoy.common.model.vo.RedisMappingVO;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/6 10:23
 * @description:
 */
public interface RedisMappingService extends EnjoyBaseService<RedisMappingDO, RedisMappingVO, RedisMappingBO> {

    RedisMappingDO getByDatabaseAndTable(RedisMappingBO redisMappingBO);
}
