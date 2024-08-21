package com.example.pongservice.service.impl;

import com.example.pongservice.service.IPongService;
import com.example.pongservice.util.PongRateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * PongServiceImpl
 *
 * @author yangjinde
 * @date 2024/8/17
 */
@Slf4j
@Service
public class PongServiceImpl implements IPongService {

    private final String lockFilePath;

    public PongServiceImpl(@Value("${ping.service.lockfile}")String lockFilePath) {
        this.lockFilePath = lockFilePath;
    }

    /**
     * pong请求入口
     *
     * @param messageMono ping入参
     * @return Mono<ResponseEntity < String>>
     */
    @Override
    public Mono<ResponseEntity<String>> pong(Mono<String> messageMono) {
        //速率控制，每秒钟最多允许1个请求通过
        if (!PongRateLimiter.checkRateLimit(lockFilePath)) {
            log.error("Too many requests in one second");
            return Mono.just(ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Too many requests in one second"));
        }
        return messageMono.flatMap(message -> {
            log.info("Received from Ping: {} & Respond World", message);
            return Mono.just(ResponseEntity.ok().body("World"));
        });
    }
}
