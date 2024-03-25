package edu.java.common.exception;

import lombok.Getter;

@Getter
public class UnsuccessfulRequestException extends RuntimeException {

    private final int statusCode;
    private final String responseBody;

    public UnsuccessfulRequestException(int statusCode, String responseBody) {
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

}
