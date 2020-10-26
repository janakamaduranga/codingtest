package com.wiley.test.portablecache.interfaces.impl;

import com.wiley.test.portablecache.exception.CacheException;
import com.wiley.test.portablecache.exception.ErrorCode;
import com.wiley.test.portablecache.interfaces.IStorageCache;

import java.io.IOException;

public class LFUStorageCache<K, V> implements IStorageCache<K, V> {
    LFUFIleUtil<K, V> fileUtil;

    public LFUStorageCache(String cacheType, int cacheSize, Class<V> type) {
        fileUtil = new LFUFIleUtil<>(cacheType, cacheSize, type);
    }

    @Override
    public V get(K key) {
        try {
            return fileUtil.getEntry(key);
        } catch (IOException e) {
            throw new CacheException(e, ErrorCode.ERROR_READING_CACHE_FILES);
        }
    }

    @Override
    public V put(K key, V value) {
        try {
            return fileUtil.addNewEntryToCache(key, value);
        } catch (IOException e) {
            throw new CacheException(e, ErrorCode.ERROR_READING_CACHE_FILES);
        }
    }

    @Override
    public V evict(K key) {
        try {
            return fileUtil.deleteEntry(key);
        } catch (IOException e) {
            throw new CacheException(e, ErrorCode.ERROR_READING_CACHE_FILES);
        }
    }

    @Override
    public void evictAll() {
        try {
            fileUtil.clearCache();
        } catch (IOException e) {
            throw new CacheException(e, ErrorCode.ERROR_READING_CACHE_FILES);
        }
    }

    @Override
    public int size() {
        throw new CacheException(ErrorCode.ERROR_READING_CACHE_FILES);
    }
}
