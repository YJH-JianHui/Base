package cn.kmdckj.base.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * 缓存工具类
 * 提供编程式缓存操作（用于Spring Cache注解无法覆盖的场景）
 */
@Slf4j
@Service
public class CacheUtil {

    @Autowired
    private CacheManager cacheManager;

    /**
     * 获取缓存
     */
    public <T> T get(String cacheName, Object key, Class<T> type) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            log.warn("缓存空间不存在: {}", cacheName);
            return null;
        }

        Cache.ValueWrapper wrapper = cache.get(key);
        if (wrapper == null) {
            return null;
        }

        Object value = wrapper.get();
        if (value == null) {
            return null;
        }

        try {
            return type.cast(value);
        } catch (ClassCastException e) {
            log.error("缓存类型转换失败: cacheName={}, key={}, expectedType={}, actualType={}",
                    cacheName, key, type.getName(), value.getClass().getName());
            return null;
        }
    }

    /**
     * 写入缓存
     */
    public void put(String cacheName, Object key, Object value) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.put(key, value);
            log.debug("写入缓存: cacheName={}, key={}", cacheName, key);
        } else {
            log.warn("缓存空间不存在，无法写入: {}", cacheName);
        }
    }

    /**
     * 删除缓存
     */
    public void evict(String cacheName, Object key) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.evict(key);
            log.debug("清除缓存: cacheName={}, key={}", cacheName, key);
        }
    }

    /**
     * 清空指定缓存空间
     */
    public void clear(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
            log.info("清空缓存空间: {}", cacheName);
        }
    }

    /**
     * 批量删除多个key
     */
    public void evictBatch(String cacheName, Collection<?> keys) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            keys.forEach(cache::evict);
            log.debug("批量清除缓存: cacheName={}, keyCount={}", cacheName, keys.size());
        }
    }

    /**
     * 判断缓存是否存在
     */
    public boolean exists(String cacheName, Object key) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            return false;
        }
        return cache.get(key) != null;
    }
}