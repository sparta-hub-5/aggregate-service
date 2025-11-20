package org.spartahub.aggregateservice.infrastructure.kafka.consumer;

import java.util.List;
import java.util.function.Consumer; // import 추가
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.spartahub.aggregateservice.dto.request.BaseTaskRequest;
import org.spartahub.aggregateservice.dto.response.TaskResultEvent;
import org.spartahub.aggregateservice.handler.ExternalTaskHandler;
import org.spartahub.aggregateservice.infrastructure.kafka.producer.TaskResultProducer; // 패키지 경로는 실제 프로젝트에 맞게 조정 필요
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ExternalTaskConsumer {

    // Raw type 대신 와일드카드 사용 권장
    private final List<ExternalTaskHandler<?>> handlers;
    private final TaskResultProducer resultProducer;
    @Bean
    public Consumer<BaseTaskRequest> taskProcessor() {
        return request -> {
            log.info("외부 작업 요청 수신 Type: {}", request.getClass().getSimpleName());

            // 1. 적절한 핸들러 찾기
            ExternalTaskHandler<?> handler = handlers.stream()
                .filter(h -> h.supports(request.getClass()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 작업 타입입니다: " + request.getClass().getName()));

            // 2. 비동기 처리 및 결과 전송
            try {
                processAndSend(handler, request);
            } catch (Exception e) {
                // common-kafka의 에러 핸들러가 잡아서 DLQ로 보냄
                throw new RuntimeException("외부 작업 처리 중 에러 발생", e);
            }
        };
    }

    /**
     * 제네릭 타입 캐스팅 문제를 해결하기 위한 헬퍼 메서드
     * supports()로 검증했으므로 강제 형변환을 수행합니다.
     */
    @SuppressWarnings("unchecked")
    private void processAndSend(ExternalTaskHandler<?> handler, BaseTaskRequest request) {
        // 여기서 (ExternalTaskHandler<BaseTaskRequest>)로 캐스팅하여 handle 호출을 가능하게 함
        ExternalTaskHandler<BaseTaskRequest> typedHandler = (ExternalTaskHandler<BaseTaskRequest>) handler;

        // WebClient는 비동기(Mono)지만, Kafka Consumer는 흐름 제어를 위해 동기적으로 결과를 기다림(block)
        TaskResultEvent result = typedHandler.handle(request).block();

        // 3. Kafka로 결과 전송
        if (result != null) {
            resultProducer.sendResult(result);
        }
    }
}