package org.spartahub.aggregateservice.domain;


import java.util.Map;

public record ExternalApiResponse(
    boolean success,
    String errorCode,
    String errorMessage,
    Map<String, Object> payload
) {}