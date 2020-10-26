package com.wiley.test.portablecache.interfaces.impl;

import com.wiley.test.common.EmployeeDto;
import com.wiley.test.portablecache.config.CacheConfig;
import com.wiley.test.portablecache.exception.CacheException;
import com.wiley.test.portablecache.exception.ErrorCode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LFUCacheTest {

    private final String empId = "123-empid";
    @Mock
    private CacheConfig config;
    @Mock
    private LFUStorageCache lfuStorageCache;
    private LFUCache<String, EmployeeDto> cache;
    private EmployeeDto employeeDto;

    @Before
    public void setUp() throws IOException {
        final String address = "Test Address";
        final String name = "Kamal";
        final int age = 23;
        employeeDto = new EmployeeDto(empId, name, address, age);
        when(config.getInMemorySize()).thenReturn(100);

        when(lfuStorageCache.get(any(String.class))).thenReturn(null);

        cache = new LFUCache<>(config, String.class.getSimpleName(), EmployeeDto.class, lfuStorageCache);
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
    public void evictall_record_success() {
        cache.put(empId, employeeDto);

        cache.evictAll();
        assertEquals(0, cache.size());
    }

    @Test
    public void evict_LFU_when_size_exceed_success() {
        when(config.getInMemorySize()).thenReturn(2);

        cache.put(empId, employeeDto);
        String empId2 = "empId2";
        EmployeeDto employeeDto2 = new EmployeeDto(empId2, "ajith", "address 2", 23);
        cache.put(empId2, employeeDto2);
        cache.get(empId);
        String empId3 = "empId3";
        EmployeeDto employeeDto3 = new EmployeeDto(empId3, "jagath", "address 3", 23);
        cache.put(empId3, employeeDto3);

        assertNull(cache.get(empId2));
        assertEquals(employeeDto, cache.get(empId));
        assertEquals(employeeDto3, cache.get(empId3));
    }

    @Test
    public void evict_LFU_when_frequency_same_size_exceed_success() {
        when(config.getInMemorySize()).thenReturn(2);

        cache.put(empId, employeeDto);
        String empId2 = "empId2";
        EmployeeDto employeeDto2 = new EmployeeDto(empId2, "ajith", "address 2", 23);
        cache.put(empId2, employeeDto2);
        cache.get(empId);
        cache.get(empId2);
        String empId3 = "empId3";
        EmployeeDto employeeDto3 = new EmployeeDto(empId3, "jagath", "address 3", 23);
        cache.put(empId3, employeeDto3);

        assertNull(cache.get(empId));
        assertEquals(employeeDto2, cache.get(empId2));
        assertEquals(employeeDto3, cache.get(empId3));
    }

    @Test
    public void throw_exception_when_size_zero() {
        when(config.getInMemorySize()).thenReturn(0);
        when(lfuStorageCache.put(any(String.class), any(EmployeeDto.class)))
                .thenThrow(new CacheException(ErrorCode.CACHE_SIZE_ZERO));
        cache = new LFUCache<>(config, String.class.getSimpleName(), EmployeeDto.class, lfuStorageCache);

        CacheException cacheException = assertThrows(CacheException.class,
                () -> cache.put(empId, employeeDto));
        assertEquals("In memory cache size is less than or equal " +
                "to zero; which is not valid", cacheException.getMessage());
    }
}
