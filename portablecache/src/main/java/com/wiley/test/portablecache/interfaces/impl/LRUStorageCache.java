package com.wiley.test.portablecache.interfaces.impl;

import com.wiley.test.portablecache.exception.CacheException;
import com.wiley.test.portablecache.exception.ErrorCode;
import com.wiley.test.portablecache.interfaces.IStorageCache;

import java.io.IOException;

public class LRUStorageCache<K, V> implements IStorageCache<K, V> {

    LRUFileUtil<K, V> lruFileUtil;
    private int storageCacheSize;
    private String cacheType;

    public LRUStorageCache(int storageCacheSize, String cacheType, Class<V> type) {
        this.storageCacheSize = storageCacheSize;
        this.cacheType = cacheType;
        lruFileUtil = new LRUFileUtil<>(cacheType, storageCacheSize, type);
    }

    @Override
    public V get(K key) {
        try {
            return lruFileUtil.getEntry(key);
        } catch (IOException e) {
            throw new CacheException(e, ErrorCode.ERROR_READING_CACHE_FILES);
        }
    }

    @Override
    public V put(K key, V value) {
        try {
            return lruFileUtil.addNewEntryToCache(key, value);
        } catch (IOException e) {
            throw new CacheException(e, ErrorCode.ERROR_READING_CACHE_FILES);
        }
    }

    @Override
    public V evict(K key) {
        try {
            return lruFileUtil.deleteEntry(key);
        } catch (IOException e) {
            throw new CacheException(e, ErrorCode.ERROR_READING_CACHE_FILES);
        }
    }

    @Override
    public void evictAll() {
        try {
            lruFileUtil.clearCache();
        } catch (IOException e) {
            throw new CacheException(e, ErrorCode.ERROR_READING_CACHE_FILES);
        }
    }

    @Override
    public int size() {
        throw new CacheException(ErrorCode.ERROR_READING_CACHE_FILES);
    }
}
