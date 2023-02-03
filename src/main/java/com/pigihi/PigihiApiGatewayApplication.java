package com.pigihi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
// @EnableEurekaClient is not showing. I think this is not needed for the latest version
@EnableDiscoveryClient
public class PigihiApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(PigihiApiGatewayApplication.class, args);
	}

}
