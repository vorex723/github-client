package com.mazurek.github_client.github;

import com.mazurek.github_client.github.dto.UserRepositoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/github")
@RequiredArgsConstructor
public class GithubController {

    private final GithubService githubService;

    @GetMapping("/users/{username}/repos")
    public ResponseEntity<List<UserRepositoryDto>> getUserRepositories(@PathVariable("username") String username) {
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(githubService.getUserRepositoriesWithBranches(username));
    }


}
