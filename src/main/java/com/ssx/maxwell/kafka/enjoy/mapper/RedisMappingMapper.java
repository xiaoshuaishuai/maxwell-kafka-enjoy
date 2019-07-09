package com.ssx.maxwell.kafka.enjoy.mapper;

import com.ssx.maxwell.kafka.enjoy.common.model.bo.RedisMappingBO;
import com.ssx.maxwell.kafka.enjoy.common.model.datao.RedisMappingDO;
import org.springframework.stereotype.Component;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/4 10:34
 * @description:
 */
@Component
public interface RedisMappingMapper extends EnjoyBaseMapper<RedisMappingDO, RedisMappingBO> {
    RedisMappingDO getByDatabaseAndTable(RedisMappingBO redisMappingBO);
}
