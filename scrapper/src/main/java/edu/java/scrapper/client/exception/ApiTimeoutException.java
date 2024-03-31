package edu.java.scrapper.client.exception;

import java.time.OffsetDateTime;
import edu.java.common.exception.HttpRequestException;
import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class ApiTimeoutException extends HttpRequestException {

    private final OffsetDateTime rateLimitResetTime;

    public ApiTimeoutException(HttpStatusCode statusCode, OffsetDateTime rateLimitResetTime) {
        super(statusCode);
        this.rateLimitResetTime = rateLimitResetTime;
    }

}
