package edu.java.scrapper.client.exception;

import java.time.OffsetDateTime;

public class ApiTimeoutException extends RuntimeException {

    private final OffsetDateTime rateLimitResetTime;

    public ApiTimeoutException(OffsetDateTime rateLimitResetTime) {
        this.rateLimitResetTime = rateLimitResetTime;
    }

    public OffsetDateTime getRateLimitResetTime() {
        return rateLimitResetTime;
    }

}
