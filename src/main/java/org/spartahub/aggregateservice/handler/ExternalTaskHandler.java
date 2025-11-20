package org.spartahub.aggregateservice.handler;

import org.spartahub.aggregateservice.dto.request.BaseTaskRequest;
import org.spartahub.aggregateservice.dto.response.TaskResultEvent;
import reactor.core.publisher.Mono;

public interface ExternalTaskHandler<T extends BaseTaskRequest> {
    // 어떤 요청 클래스를 처리할 수 있는지 확인 (와일드카드 사용)
    boolean supports(Class<?> requestClass);

    // 실제 처리 로직
    Mono<TaskResultEvent> handle(T request);
}