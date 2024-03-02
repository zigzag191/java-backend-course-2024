package edu.java.scrapper.client.exception;

import edu.java.common.dto.ApiErrorResponse;
import lombok.Getter;

@Getter
public class BadBotApiRequestException extends RuntimeException {

    private final int statusCode;
    private final ApiErrorResponse responseBody;

    public BadBotApiRequestException(int statusCode, ApiErrorResponse responseBody) {
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

}
