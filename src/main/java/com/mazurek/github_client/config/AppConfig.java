package com.mazurek.github_client.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;



@Configuration
public class AppConfig {
    private final String GITHUB_API_BASE_URL;
    private final String GITHUB_AUTH_TOKEN;

    public AppConfig(@Value("${github.api.baseUrl}") String GITHUB_API_BASE_URL,@Value("${github.token}") String GITHUB_AUTH_TOKEN) {
        this.GITHUB_API_BASE_URL = GITHUB_API_BASE_URL;
        this.GITHUB_AUTH_TOKEN = "Bearer "+GITHUB_AUTH_TOKEN;
    }

    @Bean
    public RestClient githubRestClient(){
        return RestClient.builder()
                .baseUrl(GITHUB_API_BASE_URL)
                .defaultHeader("X-GitHub-Api-Version", "2022-11-28")
                .defaultHeader(HttpHeaders.ACCEPT, "application/json")
                .defaultHeader(HttpHeaders.AUTHORIZATION, GITHUB_AUTH_TOKEN)
                .defaultHeader(HttpHeaders.USER_AGENT, "MM-Github-Client")
                .build();
    }
}
