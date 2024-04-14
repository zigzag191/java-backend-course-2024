package edu.java.scrapper.client.exception;

import java.time.OffsetDateTime;
import lombok.Getter;

@Getter
public class ApiTimeoutException extends RuntimeException {

    private final OffsetDateTime rateLimitResetTime;

    public ApiTimeoutException(OffsetDateTime rateLimitResetTime) {
        this.rateLimitResetTime = rateLimitResetTime;
    }

}
