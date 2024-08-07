package com.mazurek.github_client.github.exception;

import org.springframework.web.client.HttpServerErrorException;

import java.nio.charset.StandardCharsets;

public class GithubServerException extends HttpServerErrorException {
    public GithubServerException(HttpServerErrorException exception) {
        super(exception.getMessage(),
                exception.getStatusCode(),
                exception.getStatusText(),
                exception.getResponseHeaders(),
                exception.getResponseBodyAsByteArray(),
                StandardCharsets.UTF_8);
    }
}
