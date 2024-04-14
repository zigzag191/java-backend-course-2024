package edu.java.bot.client.exception;

import edu.java.common.dto.ApiErrorResponse;
import lombok.Getter;

@Getter
public class BadScrapperApiRequestException extends RuntimeException {

    private final int statusCode;
    private final ApiErrorResponse responseBody;

    public BadScrapperApiRequestException(int statusCode, ApiErrorResponse responseBody) {
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

}
