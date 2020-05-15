package com.example.reactive.router;

import com.example.reactive.handler.APIRequestHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class APIRouter {

    @Bean
    public RouterFunction<ServerResponse> graphqlRoute(APIRequestHandler apiRequestHandler) {
        return RouterFunctions
                .route(RequestPredicates.POST("/graphql")
                                .and(RequestPredicates.accept(MediaType.ALL)),
                        apiRequestHandler::transform);
    }
}
