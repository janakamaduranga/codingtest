package com.wiley.test.portablecache.interfaces;

/**
 * any cache implementation should extend or implement this interface
 */
public interface ICache<K, V> {
    V get(K key);

    V put(K key, V value);

    V evict(K key);

    void evictAll();

    public int size();
}
