package com.mazurek.github_client.github;

import com.mazurek.github_client.github.exception.GithubClientException;
import com.mazurek.github_client.github.exception.GithubServerException;
import lombok.RequiredArgsConstructor;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;


@Component
@RequiredArgsConstructor
public class GithubApiClient {

    private final RestClient githubRestClient;
    private final int GITHUB_DEFAULT_PAGE_SIZE = 30;

    public List<Repository> getUserRepositories(String username){
        List<Repository> repositories = new ArrayList<>();
        int pageNumber = 1;
        try{
           List<Repository> pagedRepositories;
            while(true){
                ResponseEntity<List<Repository>> response = githubRestClient.get()
                        .uri(MessageFormat.format("/users/{0}/repos?page={1}",username, pageNumber))
                        .retrieve().toEntity(new ParameterizedTypeReference<List<Repository>>() {});
                pagedRepositories = response.getBody();

                if(pagedRepositories == null)
                    break;

                repositories.addAll(pagedRepositories);

                if (pagedRepositories.isEmpty() || pagedRepositories.size() < GITHUB_DEFAULT_PAGE_SIZE)
                    break;
                pageNumber++;
            }
            return repositories;
        }
        catch (HttpClientErrorException exception){
            throw new GithubClientException(exception);
        }
        catch (HttpServerErrorException exception){
            throw new GithubServerException(exception);
        }
    }

    public List<Branch> getRepositoryBranches(Repository repository){
        List<Branch> branches = new ArrayList<>();
        int pageNumber = 1;

        try{
            List<Branch> pagedBranches;
            while(true){
                pagedBranches = githubRestClient.get()
                        .uri(MessageFormat.format("/repos/{0}/{1}/branches?page={2}", repository.getUsername(), repository.getName(), pageNumber))
                        .retrieve()
                        .body(new ParameterizedTypeReference<List<Branch>>() {});
                if (pagedBranches == null)
                    break;
                branches.addAll(pagedBranches);

                if (pagedBranches.isEmpty() || pagedBranches.size() < GITHUB_DEFAULT_PAGE_SIZE)
                    break;
                pageNumber++;
            }
            return branches;

        } catch (HttpClientErrorException exception){
            throw new GithubClientException(exception);
        } catch (HttpServerErrorException exception){
            throw new GithubServerException(exception);
        }

    }

}
