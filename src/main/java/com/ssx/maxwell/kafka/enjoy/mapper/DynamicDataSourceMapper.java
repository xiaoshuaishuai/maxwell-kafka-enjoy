package com.ssx.maxwell.kafka.enjoy.mapper;

import com.ssx.maxwell.kafka.enjoy.common.model.bo.DynamicDataSourceBO;
import com.ssx.maxwell.kafka.enjoy.common.model.db.DynamicDataSourceDO;
import org.springframework.stereotype.Component;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/4 10:34
 * @description:
 */
@Component
public interface DynamicDataSourceMapper extends EnjoyBaseMapper<DynamicDataSourceDO, DynamicDataSourceBO> {
}
