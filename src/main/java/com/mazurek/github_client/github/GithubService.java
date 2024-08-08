package com.mazurek.github_client.github;

import com.mazurek.github_client.github.dto.UserRepositoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GithubService {
    private final GithubApiClient githubApiClient;

    public List<UserRepositoryDto> getUserRepositoriesWithBranches(String username){
        List<Repository> userRepositories = githubApiClient.getUserRepositories(username);

        List<UserRepositoryDto> userRepositoriesDtos = new ArrayList<>();
        if (userRepositories.isEmpty())
            return userRepositoriesDtos;

        userRepositories.removeIf(Repository::isFork);
        for(Repository repository: userRepositories){
            List<Branch> branches = githubApiClient.getRepositoryBranches(repository);
            UserRepositoryDto repositoryDto = new UserRepositoryDto(repository, branches);
            userRepositoriesDtos.add(repositoryDto);
        }

        return userRepositoriesDtos;
    }
}
