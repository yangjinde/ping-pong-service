package com.example.pingservice.service.impl;

import com.example.pingservice.constant.Constant;
import com.example.pingservice.logger.MyLogger;
import com.example.pingservice.service.IPingService;
import com.example.pingservice.util.PingRateLimiter;
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
@Service
public class PingServiceImpl implements IPingService {

    @Value("${pong.service.url}")
    private final String pongServiceUrl;

    private final WebClient webClient;
    private final String lockFilePath;

    public PingServiceImpl(@Value("${pong.service.url}") String pongServiceUrl, WebClient.Builder webClientBuilder,
                           @Value("${ping.service.lockfile}") String lockFilePath) {
        this.pongServiceUrl = pongServiceUrl;
        this.webClient = webClientBuilder.build();
        this.lockFilePath = lockFilePath;
    }

    /**
     * run Ping
     *
     * @return Ping Result
     */
    @Override
    public Mono<ResponseEntity<String>> ping() {
        //速率控制，每秒钟最多允许2个请求通过
        if (!PingRateLimiter.checkRateLimit(lockFilePath)) {
            MyLogger.error("Request not send as being 'rate limited'.");
            return Mono.just(ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Request not send as being 'rate limited'."));
        }
        try {
            return doPing();
        } catch (Exception e) {
            MyLogger.error(e.getMessage());
            return Mono.empty(); // return a empty Mono
        }
    }

    /**
     * do Ping
     *
     * @return Ping Result
     */
    private Mono<ResponseEntity<String>> doPing() {
        return webClient.post()
                .uri(pongServiceUrl)
                .bodyValue(Constant.SAY_CONTENT)
                .exchangeToMono(this::handleResponse)
                .doOnError(IOException.class, e -> {
                    MyLogger.info("Rate limited, request not sent");
                });
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
                        MyLogger.info("Request sent:Hello & Pong Respond:" + body);
                    } else if (response.statusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                        MyLogger.error("Request send & Pong throttled it.");
                    } else {
                        MyLogger.error("Received unexpected response: " + response.statusCode());
                    }
                    return Mono.just(ResponseEntity.status(response.statusCode()).body(body));
                });
    }
}
