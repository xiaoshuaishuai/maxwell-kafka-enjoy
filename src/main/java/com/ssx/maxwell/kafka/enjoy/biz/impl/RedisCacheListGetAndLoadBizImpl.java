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
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

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
            if(!redisMapping.getRule().contains(MaxwellBinlogConstants.REDIS_RULE_2)){
                return new RespData<>(GlobalCallbackEnum.PARAMETER_RULE_ERROR.getValue(), GlobalCallbackEnum.PARAMETER_RULE_ERROR.getIntro());
            }
            //dev:test:sys_order:2:list
            RedisCacheListDTO redisCacheListDTO = redisCacheListDTOHelper.allTableRedisCacheLoadAndGet(redisMapping, dbDatabase, dbTable);
            return new RespData<>(GlobalCallbackEnum.SUCCESS.getValue(), GlobalCallbackEnum.SUCCESS.getIntro(), redisCacheListDTO);
        } else if (MaxwellBinlogConstants.KEY_ITEM.contains(key)) {
            //dev:test:sys_order:1:item:1
            if(!redisMapping.getRule().contains(MaxwellBinlogConstants.REDIS_RULE_1)){
                return new RespData<>(GlobalCallbackEnum.PARAMETER_RULE_ERROR.getValue(), GlobalCallbackEnum.PARAMETER_RULE_ERROR.getIntro());
            }
            String dbPid = keyArray[5];
            RedisCacheListDTO redisCacheListDTO = redisCacheListDTOHelper.primaryRedisCacheLoadAndGet(redisMapping, dbDatabase, dbTable, dbPid);
            return new RespData<>(GlobalCallbackEnum.SUCCESS.getValue(), GlobalCallbackEnum.SUCCESS.getIntro(), redisCacheListDTO);
        } else if (MaxwellBinlogConstants.KEY_CUSTOM.contains(key)) {
            if(Strings.isNullOrEmpty(template)){
                return new RespData<>(GlobalCallbackEnum.PARAMETER_TEMPLATE_ERROR.getValue(), GlobalCallbackEnum.PARAMETER_TEMPLATE_ERROR.getIntro());
            }
            if(!redisMapping.getRule().contains(MaxwellBinlogConstants.REDIS_RULE_3)){
                return new RespData<>(GlobalCallbackEnum.PARAMETER_RULE_ERROR.getValue(), GlobalCallbackEnum.PARAMETER_RULE_ERROR.getIntro());
            }
            String temps = redisMapping.getTemplate();
            if(Strings.isNullOrEmpty(temps) || !ArrayUtils.contains(temps.split(","), template)){
                return new RespData<>(GlobalCallbackEnum.PARAMETER_NOTEXIST_TEMPLATE_ERROR.getValue(), GlobalCallbackEnum.PARAMETER_NOTEXIST_TEMPLATE_ERROR.getIntro());
            }
            RedisCacheListDTO redisCacheListDTO = null;
            try {
                redisCacheListDTO = redisCacheListDTOHelper.customRedisCacheLoadAndGet(key, template);
            } catch (UnsupportedEncodingException e) {
                log.error("URL编码异常e={}", e);
                return new RespData<>(GlobalCallbackEnum.SYSTEM_ENCODE_ERROR.getValue(), GlobalCallbackEnum.SYSTEM_ENCODE_ERROR.getIntro());
            }
            return new RespData<>(GlobalCallbackEnum.SUCCESS.getValue(), GlobalCallbackEnum.SUCCESS.getIntro(), redisCacheListDTO);
        }else {
            return new RespData<>(GlobalCallbackEnum.KEY_NOT_RECOGNIZED.getValue(), GlobalCallbackEnum.KEY_NOT_RECOGNIZED.getIntro());
        }
    }
}
