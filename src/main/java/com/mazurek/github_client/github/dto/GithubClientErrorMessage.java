package com.mazurek.github_client.github.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)// @JsonProperty("message")
public record GithubClientErrorMessage(int status,  String message) {

    public GithubClientErrorMessage(int status, String message) {
        this.status = status;
        this.message = ifUserNotFound(status, message);
    }

    private static String ifUserNotFound(int status, String message){
        if(status == 404){
            return "Github user not found.";
        } else
            return "Github client error: " + message;
    }
}

