package edu.java.scrapper.domain.service.jpa.entity;

import jakarta.persistence.AttributeConverter;
import java.net.URI;

public class UriConverter implements AttributeConverter<URI, String> {

    @Override
    public String convertToDatabaseColumn(URI uri) {
        return uri == null ? null : uri.toString();
    }

    @Override
    public URI convertToEntityAttribute(String s) {
        return (s == null || s.isEmpty()) ? null : URI.create(s);
    }

}
