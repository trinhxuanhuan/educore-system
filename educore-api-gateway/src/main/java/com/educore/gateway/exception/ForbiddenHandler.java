package com.educore.gateway.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class ForbiddenHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Mono<Void> handle(ServerWebExchange exchange, String message) {

        try {
            Map<String, Object> error = new HashMap<>();
            error.put("timestamp", LocalDateTime.now());
            error.put("status", HttpStatus.FORBIDDEN.value());
            error.put("error", "Forbidden");
            error.put("message", message);
            error.put("path", exchange.getRequest().getPath().value());

            byte[] bytes = objectMapper.writeValueAsBytes(error);

            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            exchange.getResponse().getHeaders()
                    .setContentType(MediaType.APPLICATION_JSON);

            return exchange.getResponse()
                    .writeWith(Mono.just(exchange.getResponse()
                            .bufferFactory()
                            .wrap(bytes)));

        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }
    }
}