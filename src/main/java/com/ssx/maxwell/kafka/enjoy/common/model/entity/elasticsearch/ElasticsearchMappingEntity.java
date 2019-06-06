package com.ssx.maxwell.kafka.enjoy.common.model.entity.elasticsearch;

import com.ssx.maxwell.kafka.enjoy.common.model.entity.MappingEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author: shuaishuai.xiao
 * @date: 2019-6-6 18:17:55
 * @description: elasticsearch映射配置
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ToString(callSuper = true)
public class ElasticsearchMappingEntity extends MappingEntity {

}
