package edu.java.common.dto;

import java.util.List;

public record ListLinksResponse(
    List<LinkResponse> links,
    Long size
) {
}
