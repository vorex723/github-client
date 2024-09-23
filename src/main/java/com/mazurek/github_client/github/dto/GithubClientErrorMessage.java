package com.mazurek.github_client.github.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubClientErrorMessage(int status,  String message) {

    public GithubClientErrorMessage(int status, String message) {
        this.status = status;
        this.message = customMessage(status, message);
    }

    private static String customMessage(int status, String message){
        if(status == 404){
            return "Github user not found.";
        } else if(status == 403) {
            return "Github request limit exceeded. Comeback later.";
        }
            return "Github client error: " + message;
    }
}

