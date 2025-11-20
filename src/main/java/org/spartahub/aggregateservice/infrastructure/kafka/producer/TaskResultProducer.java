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
     * 처리 결과를 전송합니다.
     * @param event 처리 결과 이벤트 객체
     */
    void sendResult(TaskResultEvent event);
}