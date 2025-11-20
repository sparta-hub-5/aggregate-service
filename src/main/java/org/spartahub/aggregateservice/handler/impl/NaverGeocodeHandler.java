package org.spartahub.aggregateservice.handler.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.spartahub.aggregateservice.dto.request.BaseTaskRequest;
import org.spartahub.aggregateservice.dto.request.GeocodeRequest;
import org.spartahub.aggregateservice.dto.response.TaskResultEvent;
import org.spartahub.aggregateservice.handler.ExternalTaskHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;
@Slf4j
@Component
@RequiredArgsConstructor
public class NaverGeocodeHandler implements ExternalTaskHandler<GeocodeRequest> {

    private final WebClient webClient; // 전역 설정된 WebClient

    @Value("${external.naver.client-id}")
    private String clientId;

    @Value("${external.naver.client-secret}")
    private String clientSecret;

    @Override
    public boolean supports(Class<?> requestClass) {
        return GeocodeRequest.class.isAssignableFrom(requestClass);
    }

    @Override
    public boolean supports(Class<? extends BaseTaskRequest> requestClass) {
        return false;
    }

    @Override
    public Mono<TaskResultEvent> handle(GeocodeRequest request) {
        log.info("네이버 지오코딩 요청 시작: {}", request.getAddress());

        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .scheme("https")
                .host("naveropenapi.apigw.ntruss.com")
                .path("/map-geocode/v2/geocode")
                .queryParam("query", request.getAddress())
                .build())
            .header("X-NCP-APIGW-API-KEY-ID", clientId)
            .header("X-NCP-APIGW-API-KEY", clientSecret)
            .retrieve()
            .bodyToMono(Map.class) // 응답을 Map으로 받음 (DTO 따로 만들어도 됨)
            .map(response -> {
                // 네이버 응답 파싱 로직
                // (실제로는 addresses 배열 안의 x, y 값을 꺼내야 함)
                return TaskResultEvent.builder()
                    .taskId(request.getTaskId())
                    .status("SUCCESS")
                    .resultData(response) // 전체 응답 혹은 파싱된 데이터
                    .build();
            })
            .onErrorResume(e -> {
                log.error("네이버 API 호출 실패", e);
                return Mono.just(TaskResultEvent.builder()
                    .taskId(request.getTaskId())
                    .status("FAIL")
                    .resultData(e.getMessage())
                    .build());
            });
    }
}