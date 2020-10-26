package com.wiley.test.portablecache.interfaces.impl;

import com.wiley.test.common.EmployeeDto;
import com.wiley.test.portablecache.config.CacheConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LRUCacheTest {

    private final String empId = "123-empid";
    @Mock
    private CacheConfig config;
    private LRUCache<String, EmployeeDto> cache;
    private EmployeeDto employeeDto;
    @Mock
    private LRUStorageCache lruStorageCache;

    @Before
    public void setUp() throws IOException {

        cache = new LRUCache<>(config, String.class.getSimpleName(), EmployeeDto.class, lruStorageCache);
        final String address = "Test Address";
        final String name = "Kamal";
        final int age = 23;
        employeeDto = new EmployeeDto(empId, name, address, age);

        when(config.getInMemorySize()).thenReturn(100);
    }

    @Test
    public void add_cache_and_read_success() {
        cache.put(empId, employeeDto);

        EmployeeDto cacheResult = cache.get(empId);
        assertEquals(employeeDto, cacheResult);
    }

    @Test
    public void add_cache_and_read_invalid_record_fail() {
        cache.put(empId, employeeDto);

        EmployeeDto cacheResult = cache.get(empId + "Test");
        assertNull(cacheResult);
    }

    @Test
    public void evict_record_success() {
        cache.put(empId, employeeDto);

        cache.evict(empId);
        EmployeeDto cacheResult = cache.get(empId);
        assertNull(cacheResult);
    }

    @Test
    public void remove_record_success() {
        cache.put(empId, employeeDto);

        cache.remove(empId);
        EmployeeDto cacheResult = cache.get(empId);
        assertNull(cacheResult);
    }

    @Test
    public void evictall_record_success() {
        cache.put(empId, employeeDto);

        cache.evictAll();
        assertEquals(0, cache.size());
    }

    @Test
    public void evict_LRU_when_size_exceed_success() {
        when(config.getInMemorySize()).thenReturn(2);

        cache.put(empId, employeeDto);
        String empId2 = "empId2";
        EmployeeDto employeeDto2 = new EmployeeDto(empId2, "ajith", "address 2", 23);
        cache.put(empId2, employeeDto2);
        String empId3 = "empId3";
        EmployeeDto employeeDto3 = new EmployeeDto(empId3, "jagath", "address 3", 23);
        cache.put(empId3, employeeDto3);

        assertNull(cache.get(empId));
        assertEquals(employeeDto2, cache.get(empId2));
        assertEquals(employeeDto3, cache.get(empId3));
    }
}
