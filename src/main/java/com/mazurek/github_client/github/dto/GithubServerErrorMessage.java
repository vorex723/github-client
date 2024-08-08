package com.mazurek.github_client.github.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubServerErrorMessage(int status, String message) {
    private static final String PREFIX = "Github server error: ";

    public GithubServerErrorMessage(int status, String message) {
        this.status = status;
        this.message = PREFIX + message;
    }

}