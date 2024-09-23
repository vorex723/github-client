package com.mazurek.github_client.github;

import com.mazurek.github_client.github.dto.RepositoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class GithubService {
    private final GithubApiClient githubApiClient;

    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
    private final Semaphore semaphore = new Semaphore(100);


    public List<RepositoryDto> getUserRepositoriesWithBranches(String username) throws InterruptedException {
        semaphore.acquire();
        List<Repository> userRepositories = githubApiClient.getUserRepositories(username).stream().filter(repo -> !repo.isFork()).toList();
        semaphore.release();

        List<Future<RepositoryDto>> futuresRepositories = userRepositories.stream()
                .map(repository ->  executorService.submit(() -> createRepositoryDto(repository)))
                .toList();


        return futuresRepositories.stream().map(future -> {
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }

    private RepositoryDto createRepositoryDto(Repository repository) throws InterruptedException {
        semaphore.acquire();
        RepositoryDto repositoryDto = new RepositoryDto(repository, githubApiClient.getRepositoryBranches(repository));
        semaphore.release();
        return repositoryDto;
    }




}
