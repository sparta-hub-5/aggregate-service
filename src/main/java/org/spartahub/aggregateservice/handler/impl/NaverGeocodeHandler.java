package org.spartahub.aggregateservice.handler.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final WebClient webClient;

    @Value("${external.naver.client-id}")
    private String clientId;

    @Value("${external.naver.client-secret}")
    private String clientSecret;

    /**
     * [수정 완료]
     * 인터페이스의 supports(Class<?> requestClass) 시그니처와 정확히 일치시켰습니다.
     */
    @Override
    public boolean supports(Class<?> requestClass) {
        // 들어온 클래스가 GeocodeRequest이거나 그 하위 클래스인지 확인
        return GeocodeRequest.class.isAssignableFrom(requestClass);
    }

    @Override
    public Mono<TaskResultEvent> handle(GeocodeRequest request) {
        log.info("네이버 지오코딩 요청 시작: [TaskId: {}] 주소: {}", request.getTaskId(), request.getAddress());

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
            .bodyToMono(Map.class)
            .map(response -> TaskResultEvent.builder()
                .taskId(request.getTaskId())
                .status("SUCCESS")
                .resultData(response)
                .build())
            .onErrorResume(e -> {
                log.error("네이버 API 호출 실패 [TaskId: {}]", request.getTaskId(), e);
                return Mono.just(TaskResultEvent.builder()
                    .taskId(request.getTaskId())
                    .status("FAIL")
                    .resultData(e.getMessage())
                    .build());
            });
    }
}