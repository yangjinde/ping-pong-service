package com.example.pingservice.controller;

import com.example.pingservice.service.IPingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Ping控制器
 *
 * @author yangjinde
 * @date 2024/8/15
 */
@Slf4j
@RestController
@RequestMapping("/ping")
public class PingController {

    private final IPingService pingService;

    public PingController(IPingService pingService) {
        this.pingService = pingService;
    }

    @GetMapping()
    public Mono<ResponseEntity<String>> ping() {
        return pingService.ping();
    }
}
