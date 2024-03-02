package edu.java.scrapper.controller;

import edu.java.common.dto.ApiErrorResponse;
import edu.java.scrapper.controller.exception.LinkIsAlreadyTrackedException;
import edu.java.scrapper.controller.exception.LinkIsNotTrackedException;
import edu.java.scrapper.controller.exception.TgChatAlreadyExistsException;
import edu.java.scrapper.controller.exception.TgChatDoesNotExistException;
import edu.java.scrapper.controller.exception.UnsupportedResourceException;
import java.util.Arrays;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Log4j2
public class ControllerAdvice {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        return createErrorResponse(ex, HttpStatus.BAD_REQUEST, "Message is not readable");
    }

    @ExceptionHandler(LinkIsAlreadyTrackedException.class)
    public ResponseEntity<ApiErrorResponse> handleLinkIsAlreadyTracked(LinkIsAlreadyTrackedException ex) {
        return createErrorResponse(ex, HttpStatus.CONFLICT, "Link is already tracked");
    }

    @ExceptionHandler(LinkIsNotTrackedException.class)
    public ResponseEntity<ApiErrorResponse> handleLinkIsNotTracked(LinkIsNotTrackedException ex) {
        return createErrorResponse(ex, HttpStatus.NOT_FOUND, "Link is not found");
    }

    @ExceptionHandler(TgChatAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleTgChatAlreadyExists(TgChatAlreadyExistsException ex) {
        return createErrorResponse(ex, HttpStatus.CONFLICT, "Chat already exists");
    }

    @ExceptionHandler(TgChatDoesNotExistException.class)
    public ResponseEntity<ApiErrorResponse> handleTgChatDoesNotExist(TgChatDoesNotExistException ex) {
        return createErrorResponse(ex, HttpStatus.NOT_FOUND, "Chat is not found");
    }

    @ExceptionHandler(UnsupportedResourceException.class)
    public ResponseEntity<ApiErrorResponse> handleUnsupportedResource(UnsupportedResourceException ex) {
        return createErrorResponse(
            ex,
            HttpStatus.NOT_IMPLEMENTED,
            "Unable to start tracking. Resource is not supported"
        );
    }

    private ResponseEntity<ApiErrorResponse> createErrorResponse(Exception ex, HttpStatus status, String description) {
        var error = new ApiErrorResponse(
            description,
            status.toString(),
            ex.getClass().getSimpleName(),
            ex.getMessage(),
            Arrays.stream(ex.getStackTrace()).map(StackTraceElement::toString).toList()
        );
        return ResponseEntity.status(status).body(error);
    }

}
