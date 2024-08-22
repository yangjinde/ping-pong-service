package com.example.pongservice.service.impl;

import com.example.pongservice.logger.MyLogger;
import com.example.pongservice.service.IPongService;
import com.example.pongservice.util.PongRateLimiter;
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
@Service
public class PongServiceImpl implements IPongService {

    private final String lockFilePath;

    public PongServiceImpl(@Value("${ping.service.lockfile}") String lockFilePath) {
        this.lockFilePath = lockFilePath;
    }

    /**
     * pong
     *
     * @param messageMono pong param
     * @return the pong result
     */
    @Override
    public Mono<ResponseEntity<String>> pong(Mono<String> messageMono) {
        //Rate control, allowing up to 1 request to pass per second
        if (!PongRateLimiter.checkRateLimit(lockFilePath)) {
            MyLogger.error("Too many requests in one second");
            return Mono.just(ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Too many requests in one second"));
        }
        return messageMono.flatMap(message -> {
            MyLogger.info("Received from Ping: " + message + " & Respond World");
            return Mono.just(ResponseEntity.ok().body("World"));
        });
    }
}
