package com.example.pongservice.controller;

import com.example.pongservice.dto.PongResDto;
import com.example.pongservice.service.IPongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Pong Controller
 *
 * @author yangjinde
 * @date 2024/8/15
 */
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
    public Mono<PongResDto> pong(@RequestBody Mono<String> messageMono) {
        return pongService.pong(messageMono);
    }
}
