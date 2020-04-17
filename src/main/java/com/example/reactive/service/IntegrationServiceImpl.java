package com.example.reactive.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Service
public class IntegrationServiceImpl implements IntegrationService {

    @Autowired
    private ExecutorService executorService;
    Integer max = 2000;
    Integer min = 1000;
    Random random = new Random();

    @Override
    public Mono<String> doIntegrate() {
        return Mono.create(sink -> {
            executorService.submit(() -> {
                try {
                    int millis = random.nextInt(max - min) + min;
                    System.out.println(String.format("inside the doIntegrate(%o)", millis));
                    Thread.sleep(millis);
                    sink.success("Hello!");
                } catch (Exception e) {
                    throw new RuntimeException("Error");
                }
            });
        });
    }
}
