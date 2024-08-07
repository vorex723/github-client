package com.mazurek.github_client.github.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpClientErrorException;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class GithubClientException extends HttpClientErrorException {

    public GithubClientException(HttpClientErrorException exception) {
        super(
                exception.getMessage(),
                exception.getStatusCode(),
                exception.getStatusText(),
                exception.getResponseHeaders(),
                exception.getResponseBodyAsByteArray(),
                StandardCharsets.UTF_8);
    }
}
