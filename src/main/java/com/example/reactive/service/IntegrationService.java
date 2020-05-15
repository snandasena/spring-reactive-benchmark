package com.example.reactive.service;

import com.example.reactive.models.Query;
import com.fasterxml.jackson.databind.util.JSONPObject;
import reactor.core.publisher.Mono;

public interface IntegrationService {

    Mono<Object> doIntegrate(Mono<Query> query);
}
