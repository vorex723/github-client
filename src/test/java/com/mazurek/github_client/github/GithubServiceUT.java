package com.mazurek.github_client.github;

import com.mazurek.github_client.github.dto.RepositoryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("GithubService unit tests: ")
class GithubServiceUT {


    private GithubService githubService;

    @Mock
    private GithubApiClient githubApiClient;

    @Nested
    @DisplayName("Get user repositories with branches tests:")
    class GetUserRepositoriesWithBranchesTests{

        private final String username = "danvega";
        private final List<Repository> emptyRepoList = new ArrayList<>();
        private List<Repository> oneRepoList;
        private List<Repository> multipleRepoList;
        private List<Branch> firstRepoBranchList;
        private List<Branch> secondRepoBranchList;

        @BeforeEach
        void setUp() {
            githubService = new GithubService(githubApiClient);

            oneRepoList = new ArrayList<>();
            multipleRepoList = new ArrayList<>();
            firstRepoBranchList = new ArrayList<>();
            secondRepoBranchList = new ArrayList<>();
        }

        @Test
        @DisplayName("When getting user repositories should make call using GithubApiClient.")
        public void whenGettingUserRepositoriesShouldMakeCallUsingGithubApiClient() throws InterruptedException {
                githubService.getUserRepositoriesWithBranches(username);
                verify(githubApiClient, times(1)).getUserRepositories(username);
        }
        
        @Test
        @DisplayName("When user do not have repositories should return empty list.")
        public void whenUserDoNotHaveRepositoriesShouldReturnEmptyList() throws InterruptedException {
            when(githubApiClient.getUserRepositories(username)).thenReturn(emptyRepoList);

            List<RepositoryDto> returned = githubService.getUserRepositoriesWithBranches(username);

            assertThat(returned.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("When getting user repositories should remove forks from list.")
        public void whenGettingUserRepositoriesShouldRemoveForksFromList() throws InterruptedException {
            Repository forkRepository  = new Repository("firstRepo",username,true);
            multipleRepoList.add(forkRepository);
            multipleRepoList.add(new Repository("secondRepo",username ,false));
            multipleRepoList.add(new Repository("thirdRepo", username, false));

            when(githubApiClient.getUserRepositories(username)).thenReturn(multipleRepoList);

            List<RepositoryDto> output = githubService.getUserRepositoriesWithBranches(username);

            assertThat(output.size()).isEqualTo(2);
        }

        @Test
        @DisplayName("When getting user repositories should get repository branches using GithubApiClient")
        public void WhenGettingUserRepositoriesShouldGetRepositoryBranchesUsingGithubApiClient() throws InterruptedException {
            Repository firstRepo =  new Repository("firstRepo", username, false);
            Branch firstBranch = new Branch("master", "exampleSha");
            Branch secondBranch = new Branch("development", "exampleSha");
            firstRepoBranchList.add(firstBranch);
            firstRepoBranchList.add(secondBranch);
            oneRepoList.add(firstRepo);
            when(githubApiClient.getUserRepositories(username)).thenReturn(oneRepoList);
            when(githubApiClient.getRepositoryBranches(firstRepo)).thenReturn(firstRepoBranchList);

            githubService.getUserRepositoriesWithBranches(username);

            verify(githubApiClient, times(1)).getRepositoryBranches(firstRepo);
        }
        @Test
        @DisplayName("When getting user repositories should return correct amount of repositories and branches")
        public void WhenGettingUserRepositoriesShouldReturnCorrectAmountOfRepositoriesAndBranches() throws InterruptedException {
            Repository firstRepo =  new Repository("firstRepo", username, false);
            Branch firstBranch = new Branch("master", "exampleSha");
            Branch secondBranch = new Branch("development", "exampleSha");
            firstRepoBranchList.add(firstBranch);
            firstRepoBranchList.add(secondBranch);
            oneRepoList.add(firstRepo);
            when(githubApiClient.getUserRepositories(username)).thenReturn(oneRepoList);
            when(githubApiClient.getRepositoryBranches(firstRepo)).thenReturn(firstRepoBranchList);

            List<RepositoryDto> returnedRepos = githubService.getUserRepositoriesWithBranches(username);
            assertThat(returnedRepos.size()).isEqualTo(1);
            assertThat(returnedRepos.getFirst().getBranches().size()).isEqualTo(2);
        }
        @Test
        @DisplayName("When getting user repositories should return repository with empty list of branches")
        public void WhenGettingUserRepositoriesShouldReturnRepositoryWithEmptyListOfBranches() throws InterruptedException {
            Repository firstRepo =  new Repository("firstRepo", username, false);
            oneRepoList.add(firstRepo);

            when(githubApiClient.getUserRepositories(username)).thenReturn(oneRepoList);
            when(githubApiClient.getRepositoryBranches(firstRepo)).thenReturn(firstRepoBranchList);

            List<RepositoryDto> returnedRepos = githubService.getUserRepositoriesWithBranches(username);

            assertThat(returnedRepos.size()).isEqualTo(1);
            assertThat(returnedRepos.getFirst().getBranches().isEmpty()).isTrue();
        }
        @Test
        @DisplayName("When getting user repositories should return correct amount of repositories and branches")
        public void WhenGettingUserRepositoriesShouldReturnCorrectAmountOfRepositoriesAndBranches2() throws InterruptedException {
            Repository firstRepo =  new Repository("firstRepo", username, false);
            Repository secondRepo = new Repository("secondRepo", username, false);
            Branch firstBranch = new Branch("master", "exampleSha");
            Branch secondBranch = new Branch("development", "exampleSha");

            firstRepoBranchList.add(firstBranch);
            firstRepoBranchList.add(secondBranch);
            multipleRepoList.add(firstRepo);
            multipleRepoList.add(secondRepo);

            when(githubApiClient.getUserRepositories(username)).thenReturn(multipleRepoList);
            when(githubApiClient.getRepositoryBranches(firstRepo)).thenReturn(firstRepoBranchList);
            when(githubApiClient.getRepositoryBranches(secondRepo)).thenReturn(secondRepoBranchList);

            List<RepositoryDto> returnedRepos = githubService.getUserRepositoriesWithBranches(username);

            assertThat(returnedRepos.size()).isEqualTo(2);
            if(returnedRepos.getFirst().getName().equals("firstRepo")){
                assertThat(returnedRepos.getFirst().getBranches().size()).isEqualTo(2);
                assertThat(returnedRepos.getLast().getBranches().isEmpty()).isTrue();
            } else {
                assertThat(returnedRepos.getFirst().getBranches().isEmpty()).isTrue();
                assertThat(returnedRepos.getLast().getBranches().size()).isEqualTo(2);
            }

        }

        @Test
        @DisplayName("When getting user repositories should return correct amount of them.")
        public void whenGettingUserRepositoriesShouldReturnCorrectAmountOfThem() throws InterruptedException {
            Repository forkRepository  = new Repository("firstRepo",username,true);
            multipleRepoList.add(forkRepository);
            multipleRepoList.add(new Repository("secondRepo",username ,false));
            multipleRepoList.add(new Repository("thirdRepo", username, false));

            when(githubApiClient.getUserRepositories(username)).thenReturn(multipleRepoList);

            List<RepositoryDto> returnedRepos = githubService.getUserRepositoriesWithBranches(username);

            assertThat(returnedRepos.size()).isEqualTo(2);
        }

    }

}