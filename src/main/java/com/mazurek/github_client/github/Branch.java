package com.mazurek.github_client.github;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Branch {
    private String name;
    private String sha;

    @JsonProperty("commit")
    private void getCommitSha(Map<String, String> properties) {
        this.sha = properties.get("sha");
    }
}
