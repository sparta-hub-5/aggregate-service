package org.spartahub.aggregateservice.dto.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "taskType" // JSON에 "taskType": "GEOCODE" 라고 오면 알아서 매핑됨
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = GeocodeRequest.class, name = "GEOCODE"),
    @JsonSubTypes.Type(value = Directions5Request.class, name = "DIRECTIONS5")
})
public abstract class BaseTaskRequest {
    private String taskId; // 요청 추적용 ID
    private String callbackTopic; // 결과를 받을 토픽 (선택사항)
}