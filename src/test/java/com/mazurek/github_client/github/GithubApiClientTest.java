package com.mazurek.github_client.github;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.*;

@WireMockTest
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GithubApiClientTest {

    @Autowired
    private GithubApiClient githubApiClient;

    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension.newInstance()
            .options(wireMockConfig()
                    .dynamicPort()
                    .usingFilesUnderClasspath("wiremock"))
            .build();

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry dynamicPropertyRegistry){
        dynamicPropertyRegistry.add("github.api.baseUrl", wireMockServer::baseUrl);
    }

    @Nested
    @DisplayName("Get user repositories tests:")
    class getUserRepositoriesTests{
        private final String usernameNotExisting = "not-existing-user";
        private final String usernameNoRepos = "no-repos";
        private final String username = "danvega";
        private final String usernameOnePage = "danvega1page";
        private final String usernameOneFullPage = "danvega1page30";

        private final String reposUrlMessageFormat = "/users/{0}/repos?page={1}";


        @BeforeEach
        void setUp() {
        }

        @Test
        @DisplayName("")
        public void w(){


        }

    }

}