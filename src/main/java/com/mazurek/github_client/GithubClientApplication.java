package com.mazurek.github_client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class GithubClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(GithubClientApplication.class, args);
	}

}
