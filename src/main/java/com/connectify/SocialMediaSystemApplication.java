package com.connectify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SocialMediaSystemApplication {
	public static void main(String[] args) {
		SpringApplication.run(SocialMediaSystemApplication.class, args);
	}

}
