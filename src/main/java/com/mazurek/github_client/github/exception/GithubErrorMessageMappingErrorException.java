package com.mazurek.github_client.github.exception;

public class GithubErrorMessageMappingErrorException extends RuntimeException{
    public GithubErrorMessageMappingErrorException() {
        super();
    }

    public GithubErrorMessageMappingErrorException(String message) {
        super(message);
    }

    public GithubErrorMessageMappingErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public GithubErrorMessageMappingErrorException(Throwable cause) {
        super(cause);
    }

    protected GithubErrorMessageMappingErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
