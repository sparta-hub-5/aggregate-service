package org.spartahub.aggregateservice.handler.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.spartahub.aggregateservice.dto.request.Directions5Request;
import org.spartahub.aggregateservice.dto.response.TaskResultEvent;
import org.spartahub.aggregateservice.handler.ExternalTaskHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class NaverDirections5Handler implements ExternalTaskHandler<Directions5Request> {

    private final WebClient webClient;

    @Value("${external.naver.client-id}")
    private String clientId;

    @Value("${external.naver.client-secret}")
    private String clientSecret;

    @Value("${external.naver.naver-api-host}")
    private String apiHost;

    @Value("${external.naver.naver-directions5-api-path}")
    private String apiPath;

    @Override
    public boolean supports(Class<?> requestClass) {
        return Directions5Request.class.isAssignableFrom(requestClass);
    }

    @Override
    public Mono<TaskResultEvent> handle(Directions5Request request) {
        String option = Optional.ofNullable(request.getOption()).orElse("traoptimal");

        log.info("네이버 길찾기 요청 시작: [TaskId: {}] start={}, goal={}", request.getTaskId(), request.getStart(), request.getGoal());

        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .scheme("https")
                .host(apiHost)
                .path(apiPath)
                .queryParam("start", request.getStart())
                .queryParam("goal", request.getGoal())
                .queryParamIfPresent("waypoints", Optional.ofNullable(request.getWaypoints()))
                .queryParam("option", option)
                .build())
            .header("x-ncp-apigw-api-key-id", clientId)
            .header("x-ncp-apigw-api-key", clientSecret)
            .header("Accept", "application/json")
            .retrieve()
            .bodyToMono(Map.class)
            .map(response -> {
                @SuppressWarnings("unchecked")
                Map<String, Object> typedResponse = (Map<String, Object>) response;

                Object parsedData = extractRouteSummary(typedResponse, option);

                return TaskResultEvent.builder()
                    .taskId(request.getTaskId())
                    .status("SUCCESS")
                    .resultData(parsedData)
                    .build();
            })
            .onErrorResume(e -> {
                log.error("네이버 길찾기 API 호출 실패 [TaskId: {}]", request.getTaskId(), e);
                return Mono.just(TaskResultEvent.builder()
                    .taskId(request.getTaskId())
                    .status("FAIL")
                    .resultData(e.getMessage())
                    .build());
            });
    }
    @SuppressWarnings("unchecked")
    private Object extractRouteSummary(Map<String, Object> response, String option) {
        try {
            Map<String, Object> route = (Map<String, Object>) response.get("route");
            if (route == null) return "NO_ROUTE_FOUND";

            List<Map<String, Object>> routeList = (List<Map<String, Object>>) route.get(option);
            if (routeList == null || routeList.isEmpty()) return "NO_ROUTE_OPTION_FOUND";

            // 첫 번째 경로가 최적 경로
            Map<String, Object> bestRoute = routeList.getFirst();
            Map<String, Object> summary = (Map<String, Object>) bestRoute.get("summary");

            if (summary == null) return "NO_SUMMARY_FOUND";

            // 원본 데이터 추출 (네이버 API: duration=밀리초, distance=미터)
            Integer durationMs = (Integer) summary.get("duration");
            Integer distanceMeters = (Integer) summary.get("distance");

            // 1. duration: 밀리초 -> 분 (나눗셈 몫만 취함)
            long durationMinutes = durationMs != null ? durationMs / 60000 : 0;

            // 2. distance: 미터 -> km (소수점 유지를 위해 1000.0으로 나눔)
            double distanceKm = distanceMeters != null ? distanceMeters / 1000.0 : 0.0;

            // 요청하신 대로 두 필드만 반환
            return Map.of(
                "duration", durationMinutes,
                "distance", distanceKm
            );

        } catch (Exception e) {
            log.warn("길찾기 응답 파싱 중 에러 발생", e);
            return Map.of("error", "PARSING_ERROR", "original", response);
        }
    }
}