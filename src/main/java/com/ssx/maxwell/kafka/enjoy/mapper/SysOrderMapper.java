package com.ssx.maxwell.kafka.enjoy.mapper;

import com.ssx.maxwell.kafka.enjoy.common.model.bo.SysOrderBO;
import com.ssx.maxwell.kafka.enjoy.common.model.datao.SysOrderDO;
import org.springframework.stereotype.Component;

/**
 * Mapper接口
 *
 * @author shuaishuai.xiao
 * @create 2019-06-11
 */
@Component
public interface SysOrderMapper extends EnjoyBaseMapper<SysOrderDO, SysOrderBO> {
}
