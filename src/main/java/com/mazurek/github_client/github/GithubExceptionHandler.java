package com.mazurek.github_client.github;

import com.mazurek.github_client.github.dto.ErrorMessage;
import com.mazurek.github_client.github.dto.GithubClientErrorMessage;
import com.mazurek.github_client.github.dto.GithubServerErrorMessage;
import com.mazurek.github_client.github.exception.GithubClientException;
import com.mazurek.github_client.github.exception.GithubErrorMessageMappingErrorException;
import com.mazurek.github_client.github.exception.GithubServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GithubExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GithubExceptionHandler.class);

    @ExceptionHandler(GithubServerException.class)
    public ResponseEntity<GithubServerErrorMessage> handleGithubHttpServerErrors(GithubServerException exception){
       logger.error("Github server exception: {}", exception.getMessage(), exception);
        return ResponseEntity.status(exception.getStatusCode().value())
                .contentType(MediaType.APPLICATION_JSON)
                .body(exception.getResponseBodyAsGithubServerErrorMessage());
    }
    @ExceptionHandler(GithubClientException.class)
    public ResponseEntity<GithubClientErrorMessage> handleGithubHttpClientErrors(GithubClientException exception){
       logger.error("Github client exception : {}", exception.getMessage(), exception);
        return ResponseEntity.status(exception.getStatusCode().value())
                .contentType(MediaType.APPLICATION_JSON)
                .body(exception.getResponseBodyAsGithubClientErrorMessage());
    }

    @ExceptionHandler(GithubErrorMessageMappingErrorException.class)
    public ResponseEntity<ErrorMessage> handleGithubErrorMessageMappingError(GithubErrorMessageMappingErrorException exception){
        logger.error("Mapping github error exception: {}", exception.getMessage(), exception);
        return ResponseEntity.internalServerError()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Something went wrong."));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorMessage> handleRuntimeExceptions(RuntimeException exception){
        logger.error("Internal runtime exception: {}", exception.getMessage(), exception);
        return ResponseEntity.internalServerError()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Something went wrong."));
    }


}
