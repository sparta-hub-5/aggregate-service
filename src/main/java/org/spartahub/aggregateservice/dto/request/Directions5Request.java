package org.spartahub.aggregateservice.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public class Directions5Request extends BaseTaskRequest {
    private String start;

    private String goal;

    private String waypoints;

    private String option;
}