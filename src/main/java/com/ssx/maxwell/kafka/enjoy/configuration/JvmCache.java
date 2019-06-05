package com.ssx.maxwell.kafka.enjoy.configuration;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.ssx.maxwell.kafka.enjoy.common.redis.RedisMapping;
import com.ssx.maxwell.kafka.enjoy.mapper.RedisMappingMapper;
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

    private static final String BROKEN_WELL = "#";


    @Bean
    @ConditionalOnProperty(prefix = "maxwell.enjoy.redis", name = "jvmCache", havingValue = "true")
    public Cache<String, RedisMapping> redisMappingCache() {
        Cache<String, RedisMapping> cache = CacheBuilder.newBuilder().initialCapacity(10)
                .maximumSize(1000)
                .build();
        try {
            List<RedisMapping> redisMappingList = redisMappingMapper.queryList();
            if (!CollectionUtils.isEmpty(redisMappingList)) {
                Map<String, RedisMapping> mappingMap = redisMappingList.stream()
                        .collect(Collectors.toMap(redisMapping -> redisJvmCacheKey(profile, redisMapping.getDbDatabase(), redisMapping.getDbTable()),
                                r -> r, (k1, k2) -> k1));
                log.info("redis缓存信息配置,key规则=env#database#table mappingMap={}", mappingMap.toString());
                cache.putAll(mappingMap);
            }
        } catch (Exception e) {
            log.error("加载redis缓存信息配置出错,e={}", e);
            System.exit(-1);
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
