package com.example.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb;

@Configuration
public class GatewayConfig {

    @Bean
    public RouterFunction<ServerResponse> gatewayRoutes(AuthenticationFilter authenticationFilter) {
        return route("auth-service")
                .route(RequestPredicates.path("/auth/**"), http())
                .filter(lb("AUTH-SERVICE"))
                .build()
                .and(route("resource-service")
                        .route(RequestPredicates.path("/api/**"), http())
                        .filter(authenticationFilter)
                        .filter(lb("RESOURCE-SERVICE"))
                        .build());
    }
}
