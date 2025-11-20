package org.spartahub.aggregateservice.dto;

import java.time.Instant;
import java.util.Map;
import org.spartahub.aggregateservice.domain.ApiType;

public record ExternalApiRequestMessage(
    String jobId,
    String sourceService,   // "order-service", "user-service" 등
    ApiType apiType,        // NAVER, KAKAO, GOOGLE...
    Map<String, Object> payload,
    Instant requestedAt
) {}