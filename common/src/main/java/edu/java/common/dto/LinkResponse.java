package edu.java.common.dto;

import java.net.URI;

public record LinkResponse(
    Long id,
    URI url
) {
}
