package com.example.pongservice.service.impl;

import com.example.pongservice.dto.PongResDto;
import com.example.pongservice.logger.MyLogger;
import com.example.pongservice.service.IPongService;
import com.example.pongservice.util.PongRateLimiter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.nio.channels.FileLock;

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
    public Mono<PongResDto> pong(Mono<String> messageMono) {
        PongResDto pongRes = new PongResDto();
        //Rate control, allowing up to 1 request to pass per second
        FileLock fileLock = PongRateLimiter.checkRateLimit(lockFilePath);
        try {
            if (null != fileLock) {
                return messageMono.flatMap(message -> {
                    MyLogger.info("Request sent & Pong Respond.");
                    pongRes.setStatus(HttpStatus.OK.value());
                    pongRes.setBody("World");
                    return Mono.just(pongRes);
                });
            } else {
                MyLogger.error("Request send & Pong throttled it.");
                pongRes.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                pongRes.setErrorMsg("Too many requests in one second");
                return Mono.just(pongRes);
            }
        } finally {
            PongRateLimiter.releaseFileLock(fileLock);
        }
    }
}
