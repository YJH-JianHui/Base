package cn.kmdckj.base.config;

import cn.kmdckj.base.common.constant.CacheConstants;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis 配置类
 *
 * @author kmdck
 */
@Configuration
@EnableCaching
public class RedisConfig {

    /**
     * 配置 RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        Jackson2JsonRedisSerializer<Object> serializer = jackson2JsonRedisSerializer();
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * 配置 CacheManager
     * 使用 CacheConstants 中定义的常量
     */
    @Bean
    @Primary
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // 默认缓存配置
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(CacheConstants.DEFAULT_EXPIRE_TIME))
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        jackson2JsonRedisSerializer()))
                .prefixCacheNameWith(CacheConstants.CACHE_PREFIX);

        // 针对不同缓存的个性化配置
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        // 用户权限缓存
        cacheConfigurations.put(CacheConstants.CACHE_USER_PERMISSION,
                defaultConfig.entryTtl(Duration.ofSeconds(CacheConstants.PERMISSION_EXPIRE_TIME)));

        // 用户角色缓存
        cacheConfigurations.put(CacheConstants.CACHE_USER_ROLE,
                defaultConfig.entryTtl(Duration.ofSeconds(CacheConstants.PERMISSION_EXPIRE_TIME)));

        // 数据权限缓存
        cacheConfigurations.put(CacheConstants.CACHE_DATA_SCOPE,
                defaultConfig.entryTtl(Duration.ofSeconds(CacheConstants.PERMISSION_EXPIRE_TIME)));

        // 字段权限缓存
        cacheConfigurations.put(CacheConstants.CACHE_FIELD_PERMISSION,
                defaultConfig.entryTtl(Duration.ofSeconds(CacheConstants.PERMISSION_EXPIRE_TIME)));

        // 部门树缓存
        cacheConfigurations.put(CacheConstants.CACHE_DEPT_TREE,
                defaultConfig.entryTtl(Duration.ofSeconds(CacheConstants.DEPT_TREE_EXPIRE_TIME)));

        // 自定义字段定义缓存
        cacheConfigurations.put(CacheConstants.CACHE_CUSTOM_FIELD_DEFINE,
                defaultConfig.entryTtl(Duration.ofSeconds(CacheConstants.CUSTOM_FIELD_DEFINE_EXPIRE_TIME)));

        // Token缓存
        cacheConfigurations.put(CacheConstants.CACHE_LOGIN_TOKEN,
                defaultConfig.entryTtl(Duration.ofSeconds(CacheConstants.TOKEN_EXPIRE_TIME)));

        // 用户信息缓存
        cacheConfigurations.put(CacheConstants.CACHE_USER_INFO,
                defaultConfig.entryTtl(Duration.ofSeconds(CacheConstants.USER_INFO_EXPIRE_TIME)));

        return RedisCacheManager.builder(RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory))
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware()
                .build();
    }

    /**
     * Jackson序列化器
     */
    private Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL);
        mapper.registerModule(new JavaTimeModule());

        return new Jackson2JsonRedisSerializer<>(mapper, Object.class);
    }
}