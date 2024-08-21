package com.example.pongservice.controller;

import com.example.pongservice.service.IPongService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Pong控制器
 *
 * @author yangjinde
 * @date 2024/8/17
 */
@Slf4j
@RestController
@RequestMapping("/pong")
public class PongController {

    @Autowired
    private IPongService pongService;

    public PongController(IPongService pongService) {
        this.pongService = pongService;
    }

    /**
     * pong请求入口
     *
     * @param messageMono ping入参
     * @return Mono<ResponseEntity < String>>
     */
    @PostMapping
    public Mono<ResponseEntity<String>> pong(@RequestBody Mono<String> messageMono) {
        return pongService.pong(messageMono);
    }
}
