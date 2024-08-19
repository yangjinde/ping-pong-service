package com.example.pingservice.service;

import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

/**
 * IPingService
 *
 * @author yangjinde
 * @date 2024/8/3
 */
public interface IPingService {

    /**
     * 执行Ping
     *
     * @return Mono<String>
     */
    Mono<ResponseEntity<String>> ping();
}
