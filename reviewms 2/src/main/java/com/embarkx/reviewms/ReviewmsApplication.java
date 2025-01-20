package com.embarkx.reviewms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ReviewmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReviewmsApplication.class, args);
	}

}
