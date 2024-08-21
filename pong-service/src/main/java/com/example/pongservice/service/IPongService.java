package com.example.pongservice.service;

import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

/**
 * IPongService
 *
 * @author yangjinde
 * @date 2024/8/17
 */
public interface IPongService {

    /**
     * pong请求入口
     *
     * @param messageMono ping入参
     * @return Mono<ResponseEntity < String>>
     */
    Mono<ResponseEntity<String>> pong(Mono<String> messageMono);
}
