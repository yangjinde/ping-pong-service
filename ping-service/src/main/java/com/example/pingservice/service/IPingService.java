package com.example.pingservice.service;

import com.example.pingservice.dto.PingResDto;
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
    Mono<PingResDto> ping();
}
