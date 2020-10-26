package com.wiley.test.portablecache.util;

import com.wiley.test.portablecache.interfaces.InMemoryCache;
import com.wiley.test.portablecache.interfaces.impl.LRUCache;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CacheHandlerIT {
    @Autowired
    private CacheHandler cacheHandler;

    @Test
    public void getOrCreateCaches_When_Not_Exist_Then_Return_New_Success() {
        InMemoryCache<String, String> cache = cacheHandler.getOrCreateCaches(String.class.getSimpleName(), String.class, String.class);
        assertTrue(cache instanceof LRUCache);
    }

    @Test
    public void getOrCreateCaches_When_Exist_Then_Return_Existing_Success() {
        InMemoryCache<String, String> cache = cacheHandler.getOrCreateCaches(String.class.getSimpleName(), String.class, String.class);
        InMemoryCache<String, String> cacheExisting = cacheHandler.getOrCreateCaches(String.class.getSimpleName(), String.class, String.class);

        assertEquals(cache, cacheExisting);
    }

    @Test
    public void getOrCreateCaches_When_Cache_A_Value_And_Read_It_Back_Successfully() {
        InMemoryCache<String, String> cache = cacheHandler.getOrCreateCaches(String.class.getSimpleName(), String.class, String.class);

        final String empId = "111-emp-id";
        final String name = "kamal fonseka";
        cache.put(empId, name);

        assertEquals(name, cache.get(empId));
    }

    @Test
    public void getOrCreateCaches_When_Cache_A_Value_And_Read_A_Different_Fail() {
        InMemoryCache<String, String> cache = cacheHandler.getOrCreateCaches(String.class.getSimpleName(), String.class, String.class);

        final String empId = "111-emp-id";
        final String name = "kamal fonseka";
        cache.put(empId, name);

        assertNotEquals(name, cache.get(empId + "T"));

    }
}
