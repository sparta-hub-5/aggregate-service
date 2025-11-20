package org.spartahub.aggregateservice.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public class GeocodeRequest extends BaseTaskRequest {
    private String address; // 변환할 주소
}