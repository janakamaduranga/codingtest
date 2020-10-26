package com.wiley.test.portablecache.interfaces.impl;

import com.wiley.test.portablecache.config.CacheConfig;
import com.wiley.test.portablecache.interfaces.InMemoryCache;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Handle least recently used caching strategy
 *
 * @param <K>
 * @param <V>
 */
public class LRUCache<K, V> extends LinkedHashMap<K, V>
        implements InMemoryCache<K, V> {

    LRUStorageCache<K, V> lruStorageCache;
    private CacheConfig cacheConfig;
    private String cacheType;
    private Class<V> type;

    public LRUCache(CacheConfig cacheConfig, String cacheType, Class<V> type,
                    LRUStorageCache<K, V> lruStorageCache) {
        super(cacheConfig.getInMemorySize(), 0.75f, true);
        this.cacheConfig = cacheConfig;
        this.cacheType = cacheType;
        this.type = type;
        this.lruStorageCache = lruStorageCache;
    }


    @Override
    public V evict(K key) {
        V value = remove(key);
        lruStorageCache.put(key, value);
        return value;
    }

    @Override
    public V get(Object key) {
        V value = super.get(key);
        if (value == null) {
            //in memory cache should be updated
            V resultFromDb = lruStorageCache.get((K) key);
            if(resultFromDb != null)
                put((K)key, resultFromDb);
            return resultFromDb;
        }
        return value;
    }

    @Override
    public boolean remove(Object key, Object value) {
        K cacheKey = (K) key;
        V cacheValue = (V) value;
        lruStorageCache.put(cacheKey, cacheValue);
        return super.remove(key, value);
    }

    @Override
    public void evictAll() {
        clear();
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        boolean removeEldest = size() > cacheConfig.getInMemorySize();
        if (removeEldest) {
            K cacheKey = (K) eldest.getKey();
            V cacheValue = (V) eldest.getValue();
            lruStorageCache.put(cacheKey, cacheValue);
        }
        return removeEldest;
    }
}
