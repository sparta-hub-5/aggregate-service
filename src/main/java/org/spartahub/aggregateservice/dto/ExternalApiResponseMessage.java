package org.spartahub.aggregateservice.dto;

import java.time.Instant;
import java.util.Map;
import org.spartahub.aggregateservice.domain.ApiType;

public record ExternalApiResponseMessage(
    String jobId,
    ApiType apiType,
    boolean success,
    String errorCode,       // 실패 시
    String errorMessage,    // 실패 시
    Map<String, Object> payload, // 성공 시 결과
    Instant respondedAt
) {}