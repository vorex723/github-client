package com.mazurek.github_client.github;

import lombok.RequiredArgsConstructor;

import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class GithubApiClient {

    private final RestClient GithubRestClient;
    private final int GITHUB_DEFAULT_PAGE_SIZE = 30;

    public List<Repository> getUserRepositories(String username){
        List<Repository> repositories = new ArrayList<>();
        int pageNumber = 1;
        try{

        }
        catch (RuntimeException exception){

        }
        return null;
    }

}
