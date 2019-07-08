package com.ssx.maxwell.kafka.enjoy.biz.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ssx.maxwell.kafka.enjoy.biz.RedisCacheListGetAndLoadBiz;
import com.ssx.maxwell.kafka.enjoy.common.helper.RedisCacheListDTOHelper;
import com.ssx.maxwell.kafka.enjoy.common.helper.StringRedisTemplateHelper;
import com.ssx.maxwell.kafka.enjoy.common.model.RespData;
import com.ssx.maxwell.kafka.enjoy.common.model.bo.RedisMappingBO;
import com.ssx.maxwell.kafka.enjoy.common.model.db.RedisMappingDO;
import com.ssx.maxwell.kafka.enjoy.common.model.db.test.SysOrderDO;
import com.ssx.maxwell.kafka.enjoy.common.model.dto.RedisCacheListDTO;
import com.ssx.maxwell.kafka.enjoy.common.tools.JsonUtils;
import com.ssx.maxwell.kafka.enjoy.common.tools.StringUtils;
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
    public RespData<String> get(@NonNull String key, String template) {
        String value = stringRedisTemplateHelper.getValue(key);
        if (null != value) {
            log.info("缓存命中, key={}, value={}, template={}", key, value, template);
            return new RespData<>(GlobalCallbackEnum.SUCCESS.getValue(), GlobalCallbackEnum.SUCCESS.getIntro(), value);
        }
        if (!key.contains(":") || key.split(":").length < 5) {
            return new RespData<>(GlobalCallbackEnum.PARAMETER_ERROR.getValue(), GlobalCallbackEnum.PARAMETER_ERROR.getIntro());
        }
        String[] keyArray = key.split(":");
        String dbDatabase = keyArray[1];
        String dbTable = keyArray[2];
        RedisMappingBO redisMappingBO = new RedisMappingBO();
        redisMappingBO.setDbDatabase(dbDatabase).setDbTable(dbTable);
        RedisMappingDO redisMapping = redisMappingService.getByDatabaseAndTable(redisMappingBO);
        if (MaxwellBinlogConstants.KEY_LIST.endsWith(key)) {
            log.info("查询全表缓存==============key={}, template={}", key, template);
            if (!redisMapping.getRule().contains(MaxwellBinlogConstants.REDIS_RULE_2)) {
                return new RespData<>(GlobalCallbackEnum.PARAMETER_RULE_ERROR.getValue(), GlobalCallbackEnum.PARAMETER_RULE_ERROR.getIntro());
            }
            //dev:test:sys_order:2:list
            String v = redisCacheListDTOHelper.allTableRedisCacheLoadAndGet(redisMapping, dbDatabase, dbTable);
            return handleCacheThrough(key, v);
        } else if (key.contains(MaxwellBinlogConstants.KEY_ITEM)) {
            log.info("查询主键缓存==============key={}, template={}", key, template);
            //dev:test:sys_order:1:item:1
            if (!redisMapping.getRule().contains(MaxwellBinlogConstants.REDIS_RULE_1)) {
                return new RespData<>(GlobalCallbackEnum.PARAMETER_RULE_ERROR.getValue(), GlobalCallbackEnum.PARAMETER_RULE_ERROR.getIntro());
            }
            String dbPid = keyArray[5];
            String v = redisCacheListDTOHelper.primaryRedisCacheLoadAndGet(redisMapping, dbDatabase, dbTable, dbPid);
            return handleCacheThrough(key, v);
        } else if (key.contains(MaxwellBinlogConstants.KEY_CUSTOM)) {
            log.info("查询自定义缓存缓存==============key={}, template={}", key, template);
            if (Strings.isNullOrEmpty(template)) {
                return new RespData<>(GlobalCallbackEnum.PARAMETER_TEMPLATE_ERROR.getValue(), GlobalCallbackEnum.PARAMETER_TEMPLATE_ERROR.getIntro());
            }
            if (!redisMapping.getRule().contains(MaxwellBinlogConstants.REDIS_RULE_3)) {
                return new RespData<>(GlobalCallbackEnum.PARAMETER_RULE_ERROR.getValue(), GlobalCallbackEnum.PARAMETER_RULE_ERROR.getIntro());
            }
            String temps = redisMapping.getTemplate();
            if (Strings.isNullOrEmpty(temps) || !ArrayUtils.contains(temps.split(","), template)) {
                return new RespData<>(GlobalCallbackEnum.REDIS_MAPPING_NO_DEFIEND.getValue(), GlobalCallbackEnum.REDIS_MAPPING_NO_DEFIEND.getIntro());
            }
            String v = null;
            try {
                v = redisCacheListDTOHelper.customRedisCacheLoadAndGet(key, template);
            } catch (UnsupportedEncodingException e) {
                log.error("URL编码异常e={},value={}", e, v);
                return new RespData<>(GlobalCallbackEnum.SYSTEM_ENCODE_ERROR.getValue(), GlobalCallbackEnum.SYSTEM_ENCODE_ERROR.getIntro());
            }
            return handleCacheThrough(key, v);
        } else {
            log.info("查询无效==============key={}, template={}", key, template);
            return new RespData<>(GlobalCallbackEnum.KEY_NOT_RECOGNIZED.getValue(), GlobalCallbackEnum.KEY_NOT_RECOGNIZED.getIntro());
        }
    }

    @Override
    public RespData<RedisCacheListDTO> getObj(@NonNull String key, String template) {
        if (!key.contains(":") || key.split(":").length < 5) {
            return new RespData<>(GlobalCallbackEnum.PARAMETER_ERROR.getValue(), GlobalCallbackEnum.PARAMETER_ERROR.getIntro());
        }
        String[] keyArray = key.split(":");
        String dbTable = keyArray[2];

        String snakeObj = StringUtils.lineToHump(dbTable);
        if (!Strings.isNullOrEmpty(snakeObj)) {
            char a = snakeObj.charAt(0);
            //fixme 数据库对应对象DO结尾，不然找不到反序列化对象
            String finalSnakeObject = new String(String.valueOf(a)).toUpperCase() + snakeObj.substring(1) + "DO";
            RespData<String> respData = this.get(key, template);
            if (GlobalCallbackEnum.SUCCESS.getValue().equals(respData.getCode())) {
                try {
                    //todo 获取对象反射 2019-7-8 17:36:42

                    RedisCacheListDTO<SysOrderDO> redisCacheListDTO =
                            JsonUtils.getMapper().readValue("", new TypeReference<RedisCacheListDTO<SysOrderDO>>() {
                        });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    /**
     * 功能描述: 处理查询缓存穿透
     *
     * @param: []
     * @return: com.ssx.maxwell.kafka.enjoy.common.model.RespData
     * @author: shuaishuai.xiao
     * @date: 2019/7/8 16:45
     */
    private RespData handleCacheThrough(String key, String v) {
        if (null == v) {
            RedisCacheListDTO cacheListDTO = new RedisCacheListDTO().setNone(true);
            cacheListDTO.setObj(Lists.newArrayList());
            //默认过期时间1个小时
            return new RespData<>(GlobalCallbackEnum.SUCCESS.getValue(), GlobalCallbackEnum.SUCCESS.getIntro(), redisCacheListDTOHelper.setValueToRedis(key, cacheListDTO, 3600L));
        }
        return new RespData<>(GlobalCallbackEnum.SUCCESS.getValue(), GlobalCallbackEnum.SUCCESS.getIntro(), v);
    }
}
