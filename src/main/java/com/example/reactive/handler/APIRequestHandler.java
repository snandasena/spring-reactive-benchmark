package com.example.reactive.handler;

import com.example.reactive.service.IntegrationService;
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
            return ServerResponse.ok().contentType(MediaType.TEXT_PLAIN)
                    .body(integrationService.doIntegrate(), String.class);
        } catch (Exception e) {
            throw new RuntimeException("Error occurred");
        }
    }
}
