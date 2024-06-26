package edu.java.bot.controller;

import edu.java.bot.service.exception.ChatDoesNotExitsException;
import edu.java.bot.service.exception.UnsupportedUpdateTypeException;
import edu.java.common.dto.ApiErrorResponse;
import java.util.Arrays;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(Exception ex) {
        return createErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, "Unhandled exception was encountered");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        return createErrorResponse(ex, HttpStatus.BAD_REQUEST, "Message is not readable");
    }

    @ExceptionHandler(UnsupportedUpdateTypeException.class)
    public ResponseEntity<ApiErrorResponse> handleUnsupportedUpdateType(UnsupportedUpdateTypeException ex) {
        return createErrorResponse(ex, HttpStatus.NOT_IMPLEMENTED, "Unsupported update type");
    }

    @ExceptionHandler(ChatDoesNotExitsException.class)
    public ResponseEntity<ApiErrorResponse> handleChatDoesNotExist(ChatDoesNotExitsException ex) {
        return createErrorResponse(ex, HttpStatus.NOT_FOUND, "Chat does not exist");
    }

    private ResponseEntity<ApiErrorResponse> createErrorResponse(Exception ex, HttpStatus status, String description) {
        var error = new ApiErrorResponse(
            description,
            String.valueOf(status.value()),
            ex.getClass().getSimpleName(),
            ex.getMessage(),
            Arrays.stream(ex.getStackTrace()).map(StackTraceElement::toString).toList()
        );
        return ResponseEntity.status(status).body(error);
    }

}
