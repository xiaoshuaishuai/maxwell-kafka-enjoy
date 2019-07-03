package com.ssx.maxwell.kafka.enjoy.biz.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Strings;
import com.ssx.maxwell.kafka.enjoy.biz.RedisCacheListGetAndLoadBiz;
import com.ssx.maxwell.kafka.enjoy.common.helper.RedisCacheListDTOHelper;
import com.ssx.maxwell.kafka.enjoy.common.helper.StringRedisTemplateHelper;
import com.ssx.maxwell.kafka.enjoy.common.model.RespData;
import com.ssx.maxwell.kafka.enjoy.common.model.bo.RedisMappingBO;
import com.ssx.maxwell.kafka.enjoy.common.model.db.RedisMappingDO;
import com.ssx.maxwell.kafka.enjoy.common.model.dto.RedisCacheListDTO;
import com.ssx.maxwell.kafka.enjoy.common.tools.JsonUtils;
import com.ssx.maxwell.kafka.enjoy.enumerate.GlobalCallbackEnum;
import com.ssx.maxwell.kafka.enjoy.enumerate.MaxwellBinlogConstants;
import com.ssx.maxwell.kafka.enjoy.service.RedisMappingService;
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
    @Autowired
    private RedisCacheListDTOHelper redisCacheListDTOHelper;
    @Autowired
    private RedisMappingService redisMappingService;
    @Override
    public RespData<RedisCacheListDTO> get(@NonNull String key, String template) {
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

        if(!key.contains(":") || key.split(":").length < 5){
            return new RespData<>(GlobalCallbackEnum.PARAMETER_ERROR.getValue(), GlobalCallbackEnum.PARAMETER_ERROR.getIntro());
        }
        String[] keyArray = key.split(":");
        String dbDatabase = keyArray[1];
        String dbTable = keyArray[2];
        RedisMappingBO redisMappingBO = new RedisMappingBO();
        redisMappingBO.setDbDatabase(dbDatabase).setDbTable(dbTable);
        RedisMappingDO redisMapping = redisMappingService.getByDatabaseAndTable(redisMappingBO);
        if (MaxwellBinlogConstants.KEY_LIST.endsWith(key)) {
            //dev:test:sys_order:2:list
            redisCacheListDTOHelper.allTableRedisCacheLoadAndGet(redisMapping, dbDatabase, dbTable);
        } else if (MaxwellBinlogConstants.KEY_ITEM.contains(key)) {
            //dev:test:sys_order:1:item:1
            String dbPid = keyArray[5];
            redisCacheListDTOHelper.primaryRedisCacheLoadAndGet(redisMapping, dbDatabase, dbTable, dbPid);
        } else if (MaxwellBinlogConstants.KEY_CUSTOM.contains(key)) {
            //todo 2019-7-3 17:31:03 自定义缓存加载
        }
        return null;
    }
}
