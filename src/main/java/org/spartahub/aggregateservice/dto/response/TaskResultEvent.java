package org.spartahub.aggregateservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResultEvent {
    private String taskId;
    private String status; // SUCCESS, FAIL
    private Object resultData; // 위경도 정보 등 (JSON)
}