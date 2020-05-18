package com.example.it;

import com.example.reactive.util.RestResponse;
import org.junit.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class ValidateJsonSchemaTest {

    @Test
    public void testApiCall() {
        WebClient webClient = WebClient.create("http://www.mocky.io/v2");

        Mono<RestResponse> data = webClient.get()
                .uri("/5ec262d42f0000a77dc3521f")
                .retrieve()
                .bodyToMono(RestResponse.class);

        System.out.println(data.block().toString());
    }

}
