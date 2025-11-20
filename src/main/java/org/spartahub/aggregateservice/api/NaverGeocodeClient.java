package org.spartahub.aggregateservice.api;

import java.util.Map;
import org.spartahub.aggregateservice.domain.ApiType;
import org.spartahub.aggregateservice.domain.ExternalApiRequest;
import org.spartahub.aggregateservice.domain.ExternalApiResponse;
import org.spartahub.aggregateservice.dto.NaverGeocodeResponse;
import reactor.core.publisher.Mono;

public class NaverGeocodeClient implements ExternalApiClient {

    private final WebClientWrapper webClient;

    @Override
    public ApiType getApiType() {
        return ApiType.NAVER_GEOCODE;
    }

    @Override
    public Mono<ExternalApiResponse> request(ExternalApiRequest request) {
        String address = (String) request.payload().get("address");

        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/map-geocode/v2/geocode")
                .queryParam("query", address)
                .build()
            )
            .retrieve()
            .bodyToMono(NaverGeocodeResponse.class)
            .map(res -> {
                // 첫 번째 결과를 lat/lng로 매핑
                double lat = res.getFirst().getY();
                double lng = res.getFirst().getX();

                return new ExternalApiResponse(
                    true,
                    null,
                    null,
                    Map.of("lat", lat, "lng", lng)
                );
            });
    }
}