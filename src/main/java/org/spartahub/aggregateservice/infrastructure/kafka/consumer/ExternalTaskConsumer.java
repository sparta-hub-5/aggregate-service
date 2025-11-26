package org.spartahub.aggregateservice.infrastructure.kafka.consumer;

import java.util.List;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bangbang.infrastructure.kafka.consumer.BaseKafkaConsumer;
import org.spartahub.aggregateservice.dto.request.BaseTaskRequest;
import org.spartahub.aggregateservice.dto.response.TaskResultEvent;
import org.spartahub.aggregateservice.handler.ExternalTaskHandler;
import org.spartahub.aggregateservice.infrastructure.kafka.producer.TaskResultProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ExternalTaskConsumer extends BaseKafkaConsumer<BaseTaskRequest> {

    private final List<ExternalTaskHandler<?>> handlers;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private final TaskResultProducer resultProducer;

    @Bean
    public Consumer<BaseTaskRequest> taskProcessor() {
        return super.kafkaListener();
    }

    @Override
    protected void consume(BaseTaskRequest request) {
        log.info("외부 작업 요청 처리 시작 [Type: {}]", request.getClass().getSimpleName());

        ExternalTaskHandler<?> handler = handlers.stream()
            .filter(h -> h.supports(request.getClass()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 작업 타입입니다: " + request.getClass().getName()));

        try {
            processAndSend(handler, request);
        } catch (Exception e) {
            throw new RuntimeException("외부 작업 처리 중 에러 발생", e);
        }
    }

    @SuppressWarnings("unchecked")
    private void processAndSend(ExternalTaskHandler<?> handler, BaseTaskRequest request) {
        ExternalTaskHandler<BaseTaskRequest> typedHandler = (ExternalTaskHandler<BaseTaskRequest>) handler;

        TaskResultEvent result = typedHandler.handle(request).block();

        if (result != null) {
            resultProducer.sendResult(result);
        }
    }
}