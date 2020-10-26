package com.wiley.test.employee.service.testemployeeservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@Slf4j
public class TestemployeeserviceApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestemployeeserviceApplication.class, args);
    }

}
