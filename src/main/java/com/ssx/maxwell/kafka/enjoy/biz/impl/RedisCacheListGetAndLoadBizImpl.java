package com.ssx.maxwell.kafka.enjoy.biz.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Strings;
import com.ssx.maxwell.kafka.enjoy.biz.RedisCacheListGetAndLoadBiz;
import com.ssx.maxwell.kafka.enjoy.common.helper.StringRedisTemplateHelper;
import com.ssx.maxwell.kafka.enjoy.common.model.RespData;
import com.ssx.maxwell.kafka.enjoy.common.model.dto.RedisCacheListDTO;
import com.ssx.maxwell.kafka.enjoy.common.tools.JsonUtils;
import com.ssx.maxwell.kafka.enjoy.enumerate.GlobalCallbackEnum;
import com.ssx.maxwell.kafka.enjoy.enumerate.MaxwellBinlogConstants;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/7/1 14:40
 * @description:
 */
@Component
@Slf4j
public class RedisCacheListGetAndLoadBizImpl implements RedisCacheListGetAndLoadBiz {

    @Autowired
    private StringRedisTemplateHelper stringRedisTemplateHelper;

    @Override
    public RespData<RedisCacheListDTO> get(@NonNull String key) {
        String value = stringRedisTemplateHelper.getValue(key);
        if (!Strings.isNullOrEmpty(value)) {
            try {
                RedisCacheListDTO redisCacheListDTO =
                        JsonUtils.getMapper().readValue(value, new TypeReference<RedisCacheListDTO>() {
                        });
                return new RespData<>(GlobalCallbackEnum.SUCCESS.getValue(), GlobalCallbackEnum.SUCCESS.getIntro(), redisCacheListDTO);
            } catch (IOException e) {
                log.error("redis value json 转换异常e={}", e);
                return new RespData<>(GlobalCallbackEnum.JSON_PARSE_ERROR.getValue(), GlobalCallbackEnum.JSON_PARSE_ERROR.getIntro());
            }
        }
        if (MaxwellBinlogConstants.KEY_LIST.endsWith(key)) {
            //dev:test:sys_order:2:list
        } else if (MaxwellBinlogConstants.KEY_ITEM.contains(key)) {

        } else if (MaxwellBinlogConstants.KEY_CUSTOM.contains(key)) {

        }
        return null;
    }
}
