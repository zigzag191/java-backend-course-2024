package edu.java.scrapper.client.exception;

public class BadRequestException extends RuntimeException {

    private final int statusCode;

    public BadRequestException(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

}
