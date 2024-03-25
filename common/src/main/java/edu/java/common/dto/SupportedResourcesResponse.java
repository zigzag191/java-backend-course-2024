package edu.java.common.dto;

import java.net.URI;
import java.util.List;

public record SupportedResourcesResponse(List<URI> resources) {
}
