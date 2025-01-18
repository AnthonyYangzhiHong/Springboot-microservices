package com.embarkx.jobms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class JobmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobmsApplication.class, args);
	}

}
