package com.example.reactive.handler;

import com.example.reactive.graphql.Query;
import com.example.reactive.service.IntegrationService;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class APIRequestHandler {

    @Autowired
    private IntegrationService integrationService;

    public Mono<ServerResponse> transform(ServerRequest request) {
        try {
            return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                    .body(integrationService.doIntegrate(request.bodyToMono(Query.class)), JSONPObject.class);
        } catch (Exception e) {
            throw new RuntimeException("Error occurred");
        }
    }
}
