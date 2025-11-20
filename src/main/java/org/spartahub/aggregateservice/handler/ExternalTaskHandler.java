package org.spartahub.aggregateservice.handler;

import org.spartahub.aggregateservice.dto.request.BaseTaskRequest;
import org.spartahub.aggregateservice.dto.response.TaskResultEvent;
import reactor.core.publisher.Mono;

public interface ExternalTaskHandler<T extends BaseTaskRequest> {

    boolean supports(Class<?> requestClass);

    // 이 핸들러가 처리할 수 있는 클래스 타입인지 확인
    boolean supports(Class<? extends BaseTaskRequest> requestClass);

    // 실제 로직 (WebClient 사용 -> Mono 반환)
    Mono<TaskResultEvent> handle(T request);
}