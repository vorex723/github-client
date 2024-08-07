package com.mazurek.github_client.github;


import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.mazurek.github_client.github.exception.GithubClientException;
import com.mazurek.github_client.github.exception.GithubServerException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@WireMockTest
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
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
        private final String usernameSpringProject = "spring-project";
        private final String reposUrlMessageFormat = "/users/{0}/repos?page={1}";
        private int pageNumber;



        @BeforeEach
        void setUp() {
            pageNumber = 1;
        }


        @Test
        @DisplayName("When getting user repositories should call correct url.")
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
        @DisplayName("When user have less than thirty repositories should make call only for first page.")
        public void whenUserHaveLessThanThirtyRepositoriesShouldMakeCallOnlyForFirstPage(){
            githubApiClient.getUserRepositories(usernameOnePage);
            wireMockServer.verify(exactly(1), getRequestedFor(urlEqualTo(MessageFormat.format(reposUrlMessageFormat,usernameOnePage,pageNumber++))));
            wireMockServer.verify(exactly(0), getRequestedFor(urlEqualTo(MessageFormat.format(reposUrlMessageFormat,usernameOnePage,pageNumber))));
            assertThat(wireMockServer.getAllServeEvents().size()).isEqualTo(1);
        }
        @Test
        @DisplayName("When user have exactly thirty repositories should make call for second page.")
        public void whenUserHaveExactlyThirtyRepositoriesShouldMakeCallForSecondPage(){
            githubApiClient.getUserRepositories(usernameOneFullPage);
            wireMockServer.verify(exactly(1), getRequestedFor(urlEqualTo(MessageFormat.format(reposUrlMessageFormat,usernameOneFullPage,pageNumber++))));
            wireMockServer.verify(exactly(1), getRequestedFor(urlEqualTo(MessageFormat.format(reposUrlMessageFormat,usernameOneFullPage,pageNumber++))));
            wireMockServer.verify(exactly(0), getRequestedFor(urlEqualTo(MessageFormat.format(reposUrlMessageFormat,usernameOneFullPage,pageNumber))));
            assertThat(wireMockServer.getAllServeEvents().size()).isEqualTo(2);
        }

        @Test
        @DisplayName("When user have a lot repositories should look for all of them.")
        public void whenUserHaveALotRepositoriesShouldLookForAllOfThem(){
            githubApiClient.getUserRepositories(usernameALot);

            Set<String> calledUrls = new HashSet<>();
            wireMockServer.getAllServeEvents().forEach(serveEvent -> calledUrls.add(serveEvent.getRequest().getUrl()));
            assertThat(calledUrls.size()).isEqualTo(9);
            assertThat(wireMockServer.getAllServeEvents().size()).isEqualTo(9);
        }

        @Test
        @DisplayName("When user have zero repositories should return empty list.")
        public void whenUserHaveZeroRepositoriesShouldReturnEmptyList(){
            List<Repository> returnedRepos =  githubApiClient.getUserRepositories(usernameNoRepos);
            assertThat(returnedRepos.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("When getting user repositories should return as many repositories as user have.")
        public void whenGettingUserRepositoriesShouldReturnAsManyRepositoriesAsUserHave(){
            List<Repository> returnedReposOnePage =  githubApiClient.getUserRepositories(usernameOnePage);
            List<Repository> returnedReposOneFullPage =  githubApiClient.getUserRepositories(usernameOneFullPage);
            List<Repository> returnedReposALot =  githubApiClient.getUserRepositories(usernameALot);
            assertThat(returnedReposOnePage.size()).isEqualTo(29).withFailMessage("User should have exactly 29 repos.");
            assertThat(returnedReposOneFullPage.size()).isEqualTo(30).withFailMessage("User should have exactly 30 repos.");
            assertThat(returnedReposALot.size()).isEqualTo(263).withFailMessage("User should have exactly 263 repos.");
        }

        @Test
        @DisplayName("When getting user repositories should parse correct data to Repository object.")
        public void whenGettingUserRepositoriesShouldParseCorrectDataToRepositoryObject(){
            List<Repository> returnedRepos = githubApiClient.getUserRepositories(usernameSpringProject);

            assertThat(returnedRepos.size()).isEqualTo(1);
            Repository returnedRepo = returnedRepos.getFirst();

            assertEquals("muisc.store.online",returnedRepo.getName());
            assertEquals(usernameSpringProject, returnedRepo.getUsername());
            assertEquals(false,returnedRepo.isFork());

        }

    }



    @Nested
    @DisplayName("Get repository branches tests:")
    class getRepositoryBranchesTests{
        private final String branchUrlMessageFormat = "/repos/{0}/{1}/branches?page={2}";
        private final String username = "danvega";

        private final Repository repoNotExisting = new Repository("not-existing", username, false);
        private final Repository repoHttp500 = new Repository("http500", username, false);
        private final Repository repoNoBranches = new Repository("no-branches", username, false);
        private final Repository repoThirtyBranches = new Repository("text-highligther", username, false);
        private final Repository repoOneBranch = new Repository("abstracts", username, false);

        private int pageNumber = 1;

        @BeforeEach
        public void setUp(){
            pageNumber = 1;
        }

        @Test
        @DisplayName("When getting repository branches should call correct url.")
        public void whenGettingRepositoryBranchesShouldCallGoodUrl(){
            githubApiClient.getRepositoryBranches(repoOneBranch);

            wireMockServer.verify(getRequestedFor(urlEqualTo(MessageFormat.format(branchUrlMessageFormat,username,repoOneBranch.getName(),pageNumber))));
        }

        @Test
        @DisplayName("When repository does not exist should throw GithubClientException.")
        public void whenRepositoryDoesNotExistShouldThrowGithubClientException(){
            GithubClientException exception = assertThrows(GithubClientException.class ,()->githubApiClient.getRepositoryBranches(repoNotExisting));

            assertThat(exception.getStatusCode().value()).isEqualTo(404);
        }
        @Test
        @DisplayName("When github server have problem should throw GithubServerException.")
        public void whenGithubServerHaveProblemShouldThrowGithubServerException(){
            GithubServerException exception = assertThrows(GithubServerException.class ,()->githubApiClient.getRepositoryBranches(repoHttp500));

            assertThat(exception.getStatusCode().value()).isEqualTo(500);
        }

        @Test
        @DisplayName("When repository have less than thirty branches should not call second page.")
        public void whenRepositoryHaveLessThanThirtyBranchesShouldNotCallSecondPage(){
            githubApiClient.getRepositoryBranches(repoOneBranch);
            wireMockServer.verify(
                    exactly(1),
                    getRequestedFor(
                            urlEqualTo(MessageFormat.format(branchUrlMessageFormat, repoOneBranch.getUsername(),repoOneBranch.getName(),pageNumber++))));

            wireMockServer.verify(
                    exactly(0),
                    getRequestedFor(
                            urlEqualTo(MessageFormat.format(branchUrlMessageFormat, repoOneBranch.getUsername(),repoOneBranch.getName(),pageNumber))));
            assertThat(wireMockServer.getAllServeEvents().size()).isEqualTo(1);
        }

        @Test
        @DisplayName("When repository have exactly thirty branches should call second page.")
        public void whenRepositoryHaveExactlyThirtyBranchesShouldCallSecondPage(){
            githubApiClient.getRepositoryBranches(repoThirtyBranches);
            wireMockServer.verify(
                    exactly(1),
                    getRequestedFor(
                            urlEqualTo(MessageFormat.format(branchUrlMessageFormat, repoThirtyBranches.getUsername(),repoThirtyBranches.getName(),pageNumber++))));

            wireMockServer.verify(
                    exactly(1),
                    getRequestedFor(
                            urlEqualTo(MessageFormat.format(branchUrlMessageFormat, repoThirtyBranches.getUsername(),repoThirtyBranches.getName(),pageNumber))));
            assertThat(wireMockServer.getAllServeEvents().size()).isEqualTo(2);

        }

        @Test
        @DisplayName("When repository do not have branches should return empty list.")
        public void whenRepositoryDoNotHaveBranchesShouldReturnEmptyList(){
            List<Branch> returnedBranches = githubApiClient.getRepositoryBranches(repoNoBranches);
            assertNotNull(returnedBranches);
            assertTrue(returnedBranches.isEmpty());
        }

        @Test
        @DisplayName("When getting repository branches should return correct amount of branches.")
        public void whenGettingRepositoryBranchesShouldReturnCorrectAmountOfBranches(){
            List<Branch> returnedBranchesOneBranch = githubApiClient.getRepositoryBranches(repoOneBranch);
            List<Branch> returnedBranchesThirtyBranches = githubApiClient.getRepositoryBranches(repoThirtyBranches);

            assertThat(returnedBranchesOneBranch.size()).isEqualTo(1);
            assertThat(returnedBranchesThirtyBranches.size()).isEqualTo(30);
        }

        @Test
        @DisplayName("When getting repository branches should parse correct data")
        public void whenGettingRepositoryBranchesShouldParseCorrectData(){
            Repository repoDataValidation = new Repository("data-validation", username, false);
            String stubUrl = "/repos/danvega/data-validation/branches?page=1";
            String jsonBody =
                    """
                        [
                            {
                                "name":"main",
                                "commit":
                                    {
                                        "sha":"46ffd49dc1efd1224e67850495a90c8956b99d16",
                                        "url":"https://api.github.com/repos/danvega/abstracts/commits/46ffd49dc1efd1224e67850495a90c8956b99d16"
                                    },
                                "protected":false
                            }
                        ]
                    """;

            wireMockServer.stubFor(
                    get(urlEqualTo(stubUrl)).willReturn(
                            aResponse()
                                    .withBody(jsonBody)
                                    .withStatus(HttpStatus.OK.value())
                                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    ));

            List<Branch> returendList = githubApiClient.getRepositoryBranches(repoDataValidation);
            assertThat(returendList.size()).isEqualTo(1);
            Branch returendBranch = returendList.getFirst();
            assertThat(returendBranch.getName()).isEqualTo("main");
            assertThat(returendBranch.getSha()).isEqualTo("46ffd49dc1efd1224e67850495a90c8956b99d16");

        }





    }

}