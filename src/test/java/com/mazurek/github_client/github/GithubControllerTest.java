package com.mazurek.github_client.github;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.mazurek.github_client.github.dto.ErrorMessage;
import com.mazurek.github_client.github.dto.GithubClientErrorMessage;
import com.mazurek.github_client.github.dto.GithubServerErrorMessage;
import com.mazurek.github_client.github.dto.UserRepositoryDto;
import com.mazurek.github_client.github.exception.GithubClientException;
import com.mazurek.github_client.github.exception.GithubServerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(GithubExceptionHandler.class)
@DisplayName("Github controller tests: ")
class GithubControllerTest {

    @LocalServerPort
    private int port;

    private final String usernameDanvega = "danvega";
    private final String usernameNorepos = "no-repos";
    private final String usernameNotExisting = "not-existing-user";

    @Autowired
    private GithubController githubController;

    String[] contentType = {"application/json"};

    HttpClient httpClient = HttpClient.newHttpClient();
    ObjectMapper objectMapper = new ObjectMapper();
    RestClient restClient = RestClient.builder()
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .build();

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
    @DisplayName("Get user repos tests:")
    class getUserReposTests{

        @BeforeEach
        void setUp() {

        }

        @Test
        @DisplayName("When user does not have repositories should return response with http status 200 and empty list")
        public void whrenUserDoesNotHaveRepositoriesShouldReturnResponseEntityWithHttpStatus200AndEmptyList() throws Exception{
            ResponseEntity<List<UserRepositoryDto>> response = restClient.get()
                    .uri("http://localhost:" + port + "/api/v1/github/users/"+usernameNorepos+"/repos")
                    .retrieve().toEntity(new ParameterizedTypeReference<List<UserRepositoryDto>>() {});
            assertThat(response.getStatusCode().value()).isEqualTo(200);
            assertThat(response.getBody().isEmpty()).isTrue();
        }
        @Test
        @DisplayName("")
        public void when(){

        }
        @Test
        @DisplayName("When user does not have repositories should return response with http status 200 and empty list")
        public void whenUserDoesNotHaveRepositoriesShouldReturnResponseEntityWithHttpStatus200AndEmptyList() throws Exception{
            ResponseEntity<List<UserRepositoryDto>> response = restClient.get()
                    .uri("http://localhost:" + port + "/api/v1/github/users/"+usernameNorepos+"/repos")
                    .retrieve().toEntity(new ParameterizedTypeReference<List<UserRepositoryDto>>() {});
            assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
            assertThat(response.getStatusCode().value()).isEqualTo(200);
            assertThat(response.getBody().isEmpty()).isTrue();
        }

        @Test
        @DisplayName("When user does not have repositories should return response with http status 200 and list of 242 elements")
        public void whenUserDoesNotHaveRepositoriesShouldReturnResponseEntityWithHttpStatus200AndListOf242Elements() throws Exception{
            ResponseEntity<List<UserRepositoryDto>> response = restClient.get()
                    .uri("http://localhost:" + port + "/api/v1/github/users/"+usernameDanvega+"/repos")
                    .retrieve().toEntity(new ParameterizedTypeReference<List<UserRepositoryDto>>() {});

            assertThat(response.getStatusCode().value()).isEqualTo(200);
            assertThat(response.getBody().size()).isEqualTo(242);
        }

        @Test
        @DisplayName("Should return response entity with http status 404")
        public void shouldReturnResponseEntityWithHttpStatus404() throws Exception{
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                    .uri(URI.create("http://localhost:"+port+"/api/v1/github/users/"+usernameNotExisting +"/repos"))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            ErrorMessage errorMessage = objectMapper.readValue(response.body(), ErrorMessage.class);
            assertThat(response.headers().allValues("Content-Type")).isEqualTo(Arrays.stream(contentType).toList());
            assertThat(response.statusCode()).isEqualTo(404);
            assertThat(errorMessage.status()).isEqualTo(404);
            assertThat(errorMessage.message()).isEqualTo("Github user not found.");
        }

        @Test
        @DisplayName("Should return response entity with http status 500")
        public void shouldReturnResponseEntityWithHttpStatus500() throws Exception{

            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                    .uri(URI.create("http://localhost:"+port+"/api/v1/github/users/http500/repos"))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            ErrorMessage errorMessage = objectMapper.readValue(response.body(), ErrorMessage.class);

            assertThat(response.headers().allValues("Content-Type")).isEqualTo(Arrays.stream(contentType).toList());
            assertThat(response.statusCode()).isEqualTo(500);
            assertThat(errorMessage.status()).isEqualTo(500);
            assertThat(errorMessage.message()).startsWith("Github server error: ");
        }

    }

}