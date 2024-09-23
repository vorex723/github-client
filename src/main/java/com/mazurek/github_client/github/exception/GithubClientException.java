package com.mazurek.github_client.github.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mazurek.github_client.github.dto.GithubClientErrorMessage;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class GithubClientException extends HttpClientErrorException {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public GithubClientException(HttpClientErrorException exception) {
        super(
                exception.getMessage(),
                exception.getStatusCode(),
                exception.getStatusText(),
                exception.getResponseHeaders(),
                exception.getResponseBodyAsByteArray(),
                StandardCharsets.UTF_8);
    }

    public GithubClientErrorMessage getResponseBodyAsGithubClientErrorMessage() {
        try {
            return objectMapper.readValue(this.getResponseBodyAsString(), GithubClientErrorMessage.class );
        } catch (IOException e) {
            throw new GithubErrorMessageMappingErrorException(e);
        }
    }
}
