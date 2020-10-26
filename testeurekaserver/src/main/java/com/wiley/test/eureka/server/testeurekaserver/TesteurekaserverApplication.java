package com.wiley.test.eureka.server.testeurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class TesteurekaserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(TesteurekaserverApplication.class, args);
	}

}
