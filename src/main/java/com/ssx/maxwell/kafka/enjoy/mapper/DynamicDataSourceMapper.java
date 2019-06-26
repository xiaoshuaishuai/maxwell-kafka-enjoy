package com.ssx.maxwell.kafka.enjoy.mapper;

import com.ssx.maxwell.kafka.enjoy.common.model.bo.DynamicDatasourceBO;
import com.ssx.maxwell.kafka.enjoy.common.model.db.DynamicDatasourceDO;
import org.springframework.stereotype.Component;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/4 10:34
 * @description:
 */
@Component
public interface DynamicDataSourceMapper extends EnjoyBaseMapper<DynamicDatasourceDO, DynamicDatasourceBO> {
}
