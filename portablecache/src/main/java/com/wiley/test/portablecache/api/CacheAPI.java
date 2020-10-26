package com.wiley.test.portablecache.api;

import com.wiley.test.common.EmployeeDto;
import com.wiley.test.portablecache.config.CacheConfig;
import com.wiley.test.portablecache.interfaces.InMemoryCache;
import com.wiley.test.portablecache.util.CacheHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class CacheAPI {

    @Autowired
    private CacheHandler cacheHandler;

    @Autowired
    private CacheConfig cacheConfig;

    @GetMapping(path = "cache/employee/{id}")
    public ResponseEntity<EmployeeDto> getById(@PathVariable("id") String empId) {
        InMemoryCache<String, EmployeeDto> cache = cacheHandler.getOrCreateCaches(EmployeeDto.class.getSimpleName(),
                String.class, EmployeeDto.class);
        return new ResponseEntity<EmployeeDto>(cache.get(empId), HttpStatus.OK);
    }

    @PostMapping(path = "cache/employee")
    public ResponseEntity<EmployeeDto> createEmployee(@RequestBody EmployeeDto employeeDto) {
        InMemoryCache<String, EmployeeDto> cache = cacheHandler.getOrCreateCaches(EmployeeDto.class.getSimpleName(),
                String.class, EmployeeDto.class);
        cache.put(employeeDto.getId(), employeeDto);
        return new ResponseEntity<EmployeeDto>(employeeDto, HttpStatus.OK);
    }
}
