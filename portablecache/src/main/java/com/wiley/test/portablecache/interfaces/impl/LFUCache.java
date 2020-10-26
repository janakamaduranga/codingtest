package com.wiley.test.portablecache.interfaces.impl;

import com.wiley.test.portablecache.config.CacheConfig;
import com.wiley.test.portablecache.interfaces.InMemoryCache;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * handle least frequently used caching strategy
 *
 * @param <K>
 * @param <V>
 */
public class LFUCache<K, V> implements InMemoryCache<K, V> {

    Map<K, V> values;//hold key and value
    Map<K, Integer> counts;//hold key and count
    //hold count for a key in ordered manner;this is used to remove item when access frequency is same
    HashMap<Integer, Set<K>> lists;

    private CacheConfig cacheConfig;
    private String cacheType;
    private int minimum = -1;
    private Class<V> type;
    private LFUStorageCache<K, V> lfuStorageCache;

    public LFUCache(CacheConfig cacheConfig, String cacheType, Class<V> type
            , LFUStorageCache<K, V> lfuStorageCache) {
        this.cacheConfig = cacheConfig;
        this.cacheType = cacheType;
        this.type = type;
        this.lfuStorageCache = lfuStorageCache;

        values = new HashMap<>();
        counts = new HashMap<>();
        lists = new HashMap<>();
        lists.put(1, new LinkedHashSet<>());
    }

    @Override
    public V get(K key) {
        if (!values.containsKey(key)) {
            V resultFromDb = lfuStorageCache.get(key);
            if(resultFromDb != null)
                put(key, resultFromDb);
            return resultFromDb;
        }
        // get number of access count from count map
        int count = counts.get(key);
        //increase the Frequency by 1
        counts.put(key, count + 1);
        // remove item from list as its frequency is changed
        lists.get(count).remove(key);

        // if there is no element in lists for the current min, min should be next int value
        if (count == minimum && lists.get(count).size() == 0)
            minimum++;
        //if there is no LinkedHashSet for the counts; create a new one
        if (!lists.containsKey(count + 1))
            lists.put(count + 1, new LinkedHashSet<>());
        //add the key to LinkedHashSet
        lists.get(count + 1).add(key);
        return values.get(key);
    }

    @Override
    public V put(K key, V value) {
        if (cacheConfig.getInMemorySize() <= 0)
            return lfuStorageCache.put(key, value);
        // if the key is already present run the get logic to increase frequency
        if (values.containsKey(key)) {
            values.put(key, value);
            return get(key);
        }

        //if the cache is exceeding the configured value; need to LFU item
        if (values.size() >= cacheConfig.getInMemorySize()) {
            K itemToBeEvicted = lists.get(minimum).iterator().next();
            lists.get(minimum).remove(itemToBeEvicted);
            V valueToBeEvicted = values.remove(itemToBeEvicted);
            counts.remove(itemToBeEvicted);
            lfuStorageCache.put(itemToBeEvicted, valueToBeEvicted);
        }
        // as the key is new count is 1
        values.put(key, value);
        counts.put(key, 1);
        minimum = 1;
        lists.get(1).add(key);
        return value;
    }

    @Override
    public V evict(K key) {
        V value = values.remove(key);
        int count = counts.remove(key);
        lists.get(count).remove(key);
        lfuStorageCache.put(key, value);
        return value;
    }

    @Override
    public void evictAll() {
        values.clear();
        counts.clear();
        lists.clear();
        //do the initialization as cache can be used again
        lists.put(1, new LinkedHashSet<>());
    }

    @Override
    public int size() {
        return values.size();
    }
}
