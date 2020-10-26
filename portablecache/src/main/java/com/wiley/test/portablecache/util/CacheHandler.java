package com.wiley.test.portablecache.util;

import com.wiley.test.portablecache.config.CacheConfig;
import com.wiley.test.portablecache.interfaces.InMemoryCache;
import com.wiley.test.portablecache.interfaces.impl.LFUCache;
import com.wiley.test.portablecache.interfaces.impl.LFUStorageCache;
import com.wiley.test.portablecache.interfaces.impl.LRUCache;
import com.wiley.test.portablecache.interfaces.impl.LRUStorageCache;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Scope("singleton")
public class CacheHandler {

    private static final String LFU_STRATEGY = "LFU";

    private CacheConfig cacheConfig;
    private Map<String, InMemoryCache<?, ?>> caches = new HashMap<>();

    public CacheHandler(CacheConfig cacheConfig) {
        this.cacheConfig = cacheConfig;
    }

    public <K, V> InMemoryCache<K, V> getOrCreateCaches(String cacheType,
                                                        Class<K> keyClass, Class<V> valueClass) {
        InMemoryCache<K, V> cache = (InMemoryCache<K, V>) caches.get(cacheType);
        if (cache == null) {
            if (cacheConfig != null && LFU_STRATEGY.equals(cacheConfig.getCacheStrategy())) {
                LFUStorageCache<K, V> lfuStorageCache = new LFUStorageCache<>(cacheType, cacheConfig.getFileSystemSize(), valueClass);
                cache = new LFUCache<>(cacheConfig, cacheType, valueClass, lfuStorageCache);
            } else {
                LRUStorageCache<K, V> lruStorageCache = new LRUStorageCache<>(cacheConfig.getFileSystemSize(), cacheType, valueClass);
                cache = new LRUCache<>(cacheConfig, cacheType, valueClass, lruStorageCache);
            }
            caches.put(cacheType, cache);
        }
        return cache;
    }
}
