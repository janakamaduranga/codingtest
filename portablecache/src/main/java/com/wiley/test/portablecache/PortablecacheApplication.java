package com.wiley.test.portablecache;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class PortablecacheApplication {

    public static void main(String[] args) {
        SpringApplication.run(PortablecacheApplication.class, args);
    }

}
