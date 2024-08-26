package com.example.pongservice.service;

import com.example.pongservice.dto.PongResDto;
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
     * pong
     *
     * @param messageMono pong param
     * @return the pong result
     */
    Mono<PongResDto> pong(Mono<String> messageMono);
}
