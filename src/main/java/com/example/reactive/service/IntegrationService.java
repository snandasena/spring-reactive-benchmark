package com.example.reactive.service;

import com.example.reactive.graphql.Query;
import reactor.core.publisher.Mono;

public interface IntegrationService {

    Mono<Object> doIntegrate(Mono<Query> query);
}
