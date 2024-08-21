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
 * @date 2024/8/16
 */
@Slf4j
@Service
public class PingServiceImpl implements IPingService {

    @Value("${pong.service.url}")
    private final String pongServiceUrl;

    private final String SAY_CONTENT = "Hello";

    private final WebClient webClient;
    private final String lockFilePath;

    public PingServiceImpl(@Value("${pong.service.url}") String pongServiceUrl, WebClient.Builder webClientBuilder,
                           @Value("${ping.service.lockfile}")String lockFilePath) {
        this.pongServiceUrl = pongServiceUrl;
        this.webClient = webClientBuilder.build();
        this.lockFilePath = lockFilePath;
    }

    @Override
    public Mono<ResponseEntity<String>> ping() {
        //速率控制，每秒钟最多允许2个请求通过
        if (!PingRateLimiter.checkRateLimit(lockFilePath)) {
            log.error("Request not send as being 'rate limited'.");
            return Mono.just(ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Request not send as being 'rate limited'."));
        }
        try {
            return doPing();
        } catch (Exception e) {
            log.error(e.getMessage());
            return Mono.empty(); // 返回一个空的 Mono
        }
    }

    private Mono<ResponseEntity<String>> doPing() {
        return webClient.post()
                .uri(pongServiceUrl)
                .bodyValue(SAY_CONTENT)
                .exchangeToMono(this::handleResponse)
                .doOnError(IOException.class, e -> { log.info("Rate limited, request not sent"); });
    }

    /**
     * handle Pong Response
     *
     * @param response response
     * @return Mono<String>
     */
    public Mono<ResponseEntity<String>> handleResponse(ClientResponse response) {
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
