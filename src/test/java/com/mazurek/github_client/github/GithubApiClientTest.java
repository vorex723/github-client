package com.mazurek.github_client.github;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.mazurek.github_client.github.exception.GithubClientException;
import com.mazurek.github_client.github.exception.GithubServerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
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
        private final String usernameGithubServerError = "http500";
        private final String usernameNoRepos = "no-repos";
        private final String usernameALot = "danvega";
        private final String usernameOnePage = "danvega1page";
        private final String usernameOneFullPage = "danvega1page30";

        private final String reposUrlMessageFormat = "/users/{0}/repos?page={1}";
        private int pageNumber;

        @BeforeEach
        void setUp() {
            pageNumber = 1;
        }

        @Test
        @DisplayName("When getting user repositories should call good url.")
        public void whenGettingUsersRepositoriesShouldShouldCallGoodUrl(){
            githubApiClient.getUserRepositories(usernameNoRepos);
            wireMockServer.verify(exactly(1), getRequestedFor(urlEqualTo(MessageFormat.format(reposUrlMessageFormat,usernameNoRepos, pageNumber))));
        }

        @Test
        @DisplayName("When getting user repositories should throw GithubClientException if user does not exist.")
        public void whenGettingUserRepositoriesShouldThrowGithubClientExceptionIfUserDoesNotExist(){
            GithubClientException exception = assertThrows(GithubClientException.class, ()->githubApiClient.getUserRepositories(usernameNotExisting));
            assertThat(exception.getStatusCode().value()).isEqualTo(404);
        }

        @Test
        @DisplayName("When there is a problem with Github server should throw GithubServerException.")
        public void whenThereIsAProblemWithGithubServerShouldThrowGithubServerException(){
            GithubServerException  exception = assertThrows(GithubServerException.class,() -> githubApiClient.getUserRepositories(usernameGithubServerError ));
            assertThat(exception.getStatusCode().value()).isEqualTo(500);
        }

        @Test
        @DisplayName("When user have less than thirty repos should make call only for first page")
        public void whenUserHaveLessThanThirtyReposShouldMakeCallOnlyForFirstPage(){
            githubApiClient.getUserRepositories(usernameOnePage);
            wireMockServer.verify(exactly(1), getRequestedFor(urlEqualTo(MessageFormat.format(reposUrlMessageFormat,usernameOnePage,pageNumber++))));
            wireMockServer.verify(exactly(0), getRequestedFor(urlEqualTo(MessageFormat.format(reposUrlMessageFormat,usernameOnePage,pageNumber))));
            assertThat(wireMockServer.getAllServeEvents().size()).isEqualTo(1);
        }
        @Test
        @DisplayName("When user have exactly thirty repos should make call for second page")
        public void whenUserHaveExactlyThirtyReposShouldMakeCallForSecondPage(){
            githubApiClient.getUserRepositories(usernameOneFullPage);
            wireMockServer.verify(exactly(1), getRequestedFor(urlEqualTo(MessageFormat.format(reposUrlMessageFormat,usernameOneFullPage,pageNumber++))));
            wireMockServer.verify(exactly(1), getRequestedFor(urlEqualTo(MessageFormat.format(reposUrlMessageFormat,usernameOneFullPage,pageNumber++))));
            wireMockServer.verify(exactly(0), getRequestedFor(urlEqualTo(MessageFormat.format(reposUrlMessageFormat,usernameOneFullPage,pageNumber))));
            assertThat(wireMockServer.getAllServeEvents().size()).isEqualTo(2);
        }

        @Test
        @DisplayName("When user have a lot repositories should look for all of them")
        public void whenUserHaveALotRepositoriesShouldLookForAllOfThem(){
            githubApiClient.getUserRepositories(usernameALot);

            Set<String> calledUrls = new HashSet<>();
            wireMockServer.getAllServeEvents().forEach(serveEvent -> calledUrls.add(serveEvent.getRequest().getUrl()));
            assertThat(calledUrls.size()).isEqualTo(9);
            assertThat(wireMockServer.getAllServeEvents().size()).isEqualTo(9);
        }

        @Test
        @DisplayName("When user have zero repositories should return empty list")
        public void whenUserHaveZeroRepositoriesShouldReturnEmptyList(){
            List<Repository> returnedRepos =  githubApiClient.getUserRepositories(usernameNoRepos);
            assertThat(returnedRepos.isEmpty()).isTrue();

        }

        @Test
        @DisplayName("When getting user repos should return as many repositories as user have")
        public void whenGettingUserReposShouldReturnAsManyRepositoriesAsUserHave(){
            List<Repository> returnedReposOnePage =  githubApiClient.getUserRepositories(usernameOnePage);
            List<Repository> returnedReposOneFullPage =  githubApiClient.getUserRepositories(usernameOneFullPage);
            List<Repository> returnedReposALot =  githubApiClient.getUserRepositories(usernameALot);
            assertThat(returnedReposOnePage.size()).isEqualTo(29).withFailMessage("User should have exactly 29 repos.");
            assertThat(returnedReposOneFullPage.size()).isEqualTo(30).withFailMessage("User should have exactly 30 repos.");
            assertThat(returnedReposALot.size()).isEqualTo(263).withFailMessage("User should have exactly 263 repos.");
        }
    }

}