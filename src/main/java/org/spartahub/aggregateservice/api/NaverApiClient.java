package org.spartahub.aggregateservice.api;

import org.spartahub.aggregateservice.domain.ApiType;
import org.spartahub.aggregateservice.domain.ExternalApiRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import reactor.core.publisher.Mono;

@Service
public class NaverApiClient implements ExternalApiClient {
    @Override
    public ApiType getApiType() {
        return ApiType.NAVER;
    }

    @Override
    public Mono<ExternalApiClient> request(ExternalApiRequest request) {
        // Naver API 요청 로직 구현
        return Mono.just(this);
    }
}