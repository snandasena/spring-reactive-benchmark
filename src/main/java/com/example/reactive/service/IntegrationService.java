package com.example.reactive.service;

import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

public interface IntegrationService {

    Mono<String> doIntegrate();
}
