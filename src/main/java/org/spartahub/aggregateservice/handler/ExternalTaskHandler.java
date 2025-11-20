package org.spartahub.aggregateservice.handler;

import org.spartahub.aggregateservice.dto.request.BaseTaskRequest;
import org.spartahub.aggregateservice.dto.response.TaskResultEvent;
import reactor.core.publisher.Mono;

public interface ExternalTaskHandler<T extends BaseTaskRequest> {
    /**
 * Determines whether this handler can process requests of the given class.
 *
 * @param requestClass the request class to check for handler compatibility
 * @return {@code true} if this handler can handle instances of the specified class, {@code false} otherwise
 */
    boolean supports(Class<?> requestClass);

    /**
 * Processes the given task request and produces a corresponding task result.
 *
 * @param request the task request to handle
 * @return a TaskResultEvent representing the outcome of handling the request
 */
    Mono<TaskResultEvent> handle(T request);
}