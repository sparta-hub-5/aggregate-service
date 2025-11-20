package org.spartahub.aggregateservice.api;

import java.util.Map;
import org.spartahub.aggregateservice.domain.ApiType;
import org.spartahub.aggregateservice.domain.ExternalApiRequest;
import org.spartahub.aggregateservice.domain.ExternalApiResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import reactor.core.publisher.Mono;

@Service
public class NaverDirections5Client implements ExternalApiClient {

    private final RestClient restClient;

    public NaverDirections5Client(RestClient.Builder builder) {
        this.restClient = builder
            .baseUrl("https://naveropenapi.example.com") // 실제 URL로 수정
            .build();
    }

    @Override
    public ApiType getApiType() {
        return ApiType.NAVER_DIRECTIONS5;
    }

    @Override
    public Mono<ExternalApiResponse> request(ExternalApiRequest request) {

        String start = (String) request.payload().get("start");
        String goal = (String) request.payload().get("goal");

        return Mono.fromCallable(() -> {
            String responseBody = restClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/map-direction/v1/driving")
                    .queryParam("start", start)
                    .queryParam("goal", goal)
                    .build())
                .retrieve()
                .body(String.class);

            Map<String, Object> payload = Map.of("raw", responseBody);

            return new ExternalApiResponse(
                true,
                null,
                null,
                payload
            );
        });
    }
}