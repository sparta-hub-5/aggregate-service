package org.spartahub.aggregateservice.api;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.spartahub.aggregateservice.domain.ApiType;
import org.springframework.stereotype.Component;

@Component
public class ExternalApiClientRegistry {

    private final Map<ApiType, ExternalApiClient> clientMap;

    public ExternalApiClientRegistry(List<ExternalApiClient> clients) {
        this.clientMap = clients.stream()
            .collect(Collectors.toMap(ExternalApiClient::getApiType, c -> c));
    }

    public ExternalApiClient getClient(ApiType apiType) {
        return clientMap.get(apiType);
    }
}