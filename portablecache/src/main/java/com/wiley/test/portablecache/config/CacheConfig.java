package com.wiley.test.portablecache.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@RefreshScope
public class CacheConfig {

    private String cacheStrategy;
    private int inMemorySize;
    private int fileSystemSize;


    public CacheConfig(@Value("${cache.strategy:LRU}") String cacheStrategy,
                       @Value("${cache.inmemory.size:100}") int inMemorySize,
                       @Value("${cache.filesystem.size:200}") int fileSystemSize) {
        this.cacheStrategy = cacheStrategy;
        this.inMemorySize = inMemorySize;
        this.fileSystemSize = fileSystemSize;
    }
}
