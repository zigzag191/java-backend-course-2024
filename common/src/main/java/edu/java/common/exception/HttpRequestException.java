package edu.java.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class HttpRequestException extends RuntimeException {

    private final HttpStatusCode statusCode;

    public HttpRequestException(HttpStatusCode statusCode) {
        this.statusCode = statusCode;
    }

}
