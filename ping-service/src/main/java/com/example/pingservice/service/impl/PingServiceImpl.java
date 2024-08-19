package com.example.pingservice.service.impl;

import com.example.pingservice.service.IPingService;
import com.example.pingservice.util.PingRateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;

/**
 * PingServiceImpl
 *
 * @author yangjinde
 * @date 2024/8/3
 */
@Slf4j
@Service
public class PingServiceImpl implements IPingService {

    @Value("${pong.service.url}")
    private String pongServiceUrl;

    private final String SAY_CONTENT = "Hello";

    private final WebClient webClient = WebClient.create();

    @Override
    public Mono<ResponseEntity<String>> ping() {
        //速率控制，每秒钟最多允许2个请求通过
        if (!PingRateLimiter.checkRateLimit()) {
            log.error("Request not send as being 'rate limited'.");
            return Mono.just(ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Request not send as being 'rate limited'."));
        }
        try {
            return webClient.post()
                    .uri(pongServiceUrl)
                    .bodyValue(SAY_CONTENT)
                    .exchangeToMono(this::handleResponse)
                    .doOnError(IOException.class, e -> {
                        log.info("Rate limited, request not sent");
                    });
        } catch (Exception e) {
            log.error(e.getMessage());
            return Mono.empty(); // 返回一个空的 Mono
        }
    }

    /**
     * handle Pong Response
     *
     * @param response response
     * @return Mono<String>
     */
    private Mono<ResponseEntity<String>> handleResponse(ClientResponse response) {
        return response.bodyToMono(String.class)
                .flatMap(body -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        log.info("Request sent:{} & Pong Respond:{}.", SAY_CONTENT, body);
                    } else if (response.statusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                        log.error("Request send & Pong throttled it.");
                    } else {
                        log.error("Received unexpected response: {}. ", response.statusCode());
                    }
                    return Mono.just(ResponseEntity.status(response.statusCode()).body(body));
                });
    }
}
