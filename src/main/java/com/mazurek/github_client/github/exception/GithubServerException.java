package com.mazurek.github_client.github.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mazurek.github_client.github.dto.GithubServerErrorMessage;
import org.springframework.web.client.HttpServerErrorException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class GithubServerException extends HttpServerErrorException {
    private final ObjectMapper objectMapper = new ObjectMapper();
    public GithubServerException(HttpServerErrorException exception) {
        super(exception.getMessage(),
                exception.getStatusCode(),
                exception.getStatusText(),
                exception.getResponseHeaders(),
                exception.getResponseBodyAsByteArray(),
                StandardCharsets.UTF_8);
    }

    public GithubServerErrorMessage getResponseBodyAsGithubServerErrorMessage(){
        try {
            return objectMapper.readValue(this.getResponseBodyAsString(), GithubServerErrorMessage.class );
        } catch (IOException e) {
            throw new GithubErrorMessageMappingErrorException(e);
        }
    }
}
