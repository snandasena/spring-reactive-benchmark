package com.example.reactive.service;

import reactor.core.publisher.Mono;

public interface IntegrationService {

    Mono<String> doIntegrate();
}
