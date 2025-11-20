package org.spartahub.aggregateservice.domain;

import java.util.Map;

public record ExternalApiRequest(
    ApiType apiType,
    Map<String, Object> payload
) {}