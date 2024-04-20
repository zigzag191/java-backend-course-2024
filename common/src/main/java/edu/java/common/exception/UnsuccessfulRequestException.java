package edu.java.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class UnsuccessfulRequestException extends HttpRequestException {

    private final String responseBody;

    public UnsuccessfulRequestException(HttpStatusCode statusCode, String responseBody) {
        super(statusCode);
        this.responseBody = responseBody;
    }

}
