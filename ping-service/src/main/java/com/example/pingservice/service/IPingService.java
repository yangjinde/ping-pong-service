package com.example.pingservice.service;

import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

/**
 * IPingService
 *
 * @author yangjinde
 * @date 2024/8/16
 */
public interface IPingService {

    /**
     * run Ping
     *
     * @return Ping Result
     */
    Mono<ResponseEntity<String>> ping();
}
