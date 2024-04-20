package edu.java.scrapper.domain.service.exception;

public class KafkaException extends RuntimeException {

    public KafkaException(String message, Throwable cause) {
        super(message, cause);
    }

}
