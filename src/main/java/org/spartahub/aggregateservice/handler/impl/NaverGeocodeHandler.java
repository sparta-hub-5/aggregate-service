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

import java.util.List;
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

    @Value("${external.naver.naver-api-host}")
    private String apiHost;


    @Value("${external.naver.naver-geo-api-path}")
    private String apiPath;

    @Override
    public boolean supports(Class<?> requestClass) {
        return GeocodeRequest.class.isAssignableFrom(requestClass);
    }

    @Override
    public Mono<TaskResultEvent> handle(GeocodeRequest request) {
        log.info("네이버 지오코딩 요청 시작: [TaskId: {}] 주소: {}", request.getTaskId(), request.getAddress());

        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .scheme("https")
                .host(apiHost)
                .path(apiPath)
                .queryParam("query", request.getAddress())
                .build())
            .header("x-ncp-apigw-api-key-id", clientId)
            .header("x-ncp-apigw-api-key", clientSecret)
            .header("Accept", "application/json")
            .retrieve()
            .bodyToMono(Map.class)
            .map(response -> {
                // Map을 안전하게 캐스팅하기 위한 헬퍼 메서드 호출
                // (여기서 발생하는 경고는 아래 메서드 내부에서 제어됨)
                @SuppressWarnings("unchecked")
                Map<String, Object> typedResponse = (Map<String, Object>) response;
                Object parsedData = extractCoordinates(typedResponse);

                return TaskResultEvent.builder()
                    .taskId(request.getTaskId())
                    .status("SUCCESS")
                    .resultData(parsedData)
                    .build();
            })
            .onErrorResume(e -> {
                log.error("네이버 API 호출 실패 [TaskId: {}]", request.getTaskId(), e);
                return Mono.just(TaskResultEvent.builder()
                    .taskId(request.getTaskId())
                    .status("FAIL")
                    .resultData(e.getMessage())
                    .build());
            });
    }

    /**
     * 네이버 응답 Map에서 좌표(x, y)만 추출하는 헬퍼 메서드
     * Unchecked cast 경고를 억제합니다.
     */
    @SuppressWarnings("unchecked")
    private Object extractCoordinates(Map<String, Object> response) {
        try {
            // 여기서 발생하는 'List<?> -> List<Map...>' 형변환 경고를 억제함
            List<Map<String, Object>> addresses = (List<Map<String, Object>>) response.get("addresses");

            if (addresses != null && !addresses.isEmpty()) {
                Map<String, Object> firstMatch = addresses.get(0);
                String x = (String) firstMatch.get("x"); // 경도
                String y = (String) firstMatch.get("y"); // 위도
                String roadAddress = (String) firstMatch.get("roadAddress");

                return Map.of(
                    "x", x,
                    "y", y,
                    "roadAddress", roadAddress
                );
            }
            return "NO_MATCH_FOUND";
        } catch (Exception e) {
            log.warn("응답 파싱 중 에러 발생", e);
            return response; // 파싱 실패 시 원본 반환
        }
    }
}