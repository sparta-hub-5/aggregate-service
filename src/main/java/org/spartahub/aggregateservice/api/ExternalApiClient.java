package org.spartahub.aggregateservice.api;

import org.spartahub.aggregateservice.domain.ApiType;
import org.spartahub.aggregateservice.domain.ExternalApiRequest;
import org.spartahub.aggregateservice.domain.ExternalApiResponse;
import reactor.core.publisher.Mono;

public interface ExternalApiClient {
    ApiType getApiType();
    Mono<ExternalApiResponse> request(ExternalApiRequest request);
}