package com.example.pingservice.controller;

import com.example.pingservice.dto.PingResDto;
import com.example.pingservice.service.IPingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Ping Controller
 *
 * @author yangjinde
 * @date 2024/8/15
 */
@RestController
@RequestMapping("/ping")
public class PingController {

    private final IPingService pingService;

    public PingController(IPingService pingService) {
        this.pingService = pingService;
    }

    /**
     * ping
     *
     * @return ping result
     */
    @GetMapping()
    public Mono<PingResDto> ping() {
        return pingService.ping();
    }
}
