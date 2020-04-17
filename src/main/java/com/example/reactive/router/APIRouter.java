package com.example.reactive.router;

import com.example.reactive.handler.APIRequestHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;

@Configuration
public class APIRouter {

    @Bean
    public RouterFunction<ServerResponse> route(APIRequestHandler apiRequestHandler) {
        return RouterFunctions
                .route(RequestPredicates.all().and(RequestPredicates.accept(MediaType.ALL)),
                        apiRequestHandler::transform);
    }
}
