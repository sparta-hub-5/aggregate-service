package org.spartahub.aggregateservice.infrastructure.kafka.producer;

import org.bangbang.infrastructure.kafka.annotation.KafkaClient;
import org.spartahub.aggregateservice.dto.response.TaskResultEvent;

/**
 * 외부 작업 처리 결과를 Kafka로 전송하는 Producer
 * 공용 라이브러리의 @KafkaClient를 사용하여 구현체 없이 동작합니다.
 */
// application.yml의 spring.cloud.stream.bindings.task-result-out-0 와 매핑됩니다.
@KafkaClient("task-result-out-0")
public interface TaskResultProducer {

    /**
 * Publish a TaskResultEvent to the configured Kafka destination (bound to "task-result-out-0").
 *
 * @param event the TaskResultEvent payload to send
 */
    void sendResult(TaskResultEvent event);
}