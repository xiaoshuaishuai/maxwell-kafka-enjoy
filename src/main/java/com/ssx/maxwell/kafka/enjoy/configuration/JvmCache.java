package com.ssx.maxwell.kafka.enjoy.configuration;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.ssx.maxwell.kafka.enjoy.common.model.datao.RedisMappingDO;
import com.ssx.maxwell.kafka.enjoy.common.tools.ClassUtils;
import com.ssx.maxwell.kafka.enjoy.mapper.RedisMappingMapper;
import com.sun.org.apache.regexp.internal.RE;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/4 13:30
 * @description: jvm内部缓存
 */
@Slf4j
@Configuration
public class JvmCache {

    @Autowired
    private RedisMappingMapper redisMappingMapper;

    @Value("${spring.profiles.active:dev}")
    private String profile;

    public static final String BROKEN_WELL = "#";
    public static final String DATAOBJECT_PKG = "com.ssx.maxwell.kafka.enjoy.common.model.datao";


    @Bean
    @ConditionalOnProperty(prefix = "maxwell.enjoy.redis", name = "jvmCache", havingValue = "true")
    public Cache<String, RedisMappingDO> redisMappingCache() {
        Cache<String, RedisMappingDO> cache = CacheBuilder.newBuilder().initialCapacity(10)
                .maximumSize(1000)
                .build();
        try {
            List<RedisMappingDO> redisMappingList = redisMappingMapper.list();
            if (!CollectionUtils.isEmpty(redisMappingList)) {
                Map<String, RedisMappingDO> mappingMap = redisMappingList.stream()
                        .collect(Collectors.toMap(redisMapping -> redisJvmCacheKey(profile, redisMapping.getDbDatabase(), redisMapping.getDbTable()),
                                r -> r, (k1, k2) -> k1));
                cache.putAll(mappingMap);
            }
        } catch (Exception e) {
            log.error("加载redis缓存信息配置出错,e={}", e);
            System.exit(-1);
        }
        return cache;
    }

    @Bean
    public Cache<String, Class<?>> dataObjectCache() {
        List<Class<?>> classList = ClassUtils.getClasses(DATAOBJECT_PKG);
        Cache<String, Class<?>> cache = CacheBuilder.newBuilder().initialCapacity(10)
                .maximumSize(1000)
                .build();
        if (!CollectionUtils.isEmpty(classList)) {
            classList.forEach(cls -> {
                log.info("DO cache simpleName={}", cls.getSimpleName());
                cache.put(cls.getSimpleName(), cls);
            });
        }
        return cache;
    }

    /**
     * 功能描述: jvm RedisMapping cache key
     *
     * @param: [profile, database, table]
     * @return: java.lang.String
     * @author: shuaishuai.xiao
     * @date: 2019/6/4 15:47
     */
    public static String redisJvmCacheKey(String profile, String database, String table) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(profile).append(BROKEN_WELL).append(database).append(BROKEN_WELL).append(table);
        return stringBuilder.toString();
    }
}
