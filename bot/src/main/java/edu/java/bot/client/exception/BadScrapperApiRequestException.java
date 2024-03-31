package edu.java.bot.client.exception;

import edu.java.common.dto.ApiErrorResponse;
import edu.java.common.exception.HttpRequestException;
import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class BadScrapperApiRequestException extends HttpRequestException {

    private final ApiErrorResponse responseBody;

    public BadScrapperApiRequestException(HttpStatusCode statusCode, ApiErrorResponse responseBody) {
        super(statusCode);
        this.responseBody = responseBody;
    }

}
