package com.example.pongservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Pong Server Start Class
 *
 * @author yangjinde
 * @date 2024/8/15
 */
@SpringBootApplication
public class PongApplication {

    /**
     * Launches the Pong application
     *
     * @param args args
     */
    public static void main(String[] args) {
        SpringApplication.run(PongApplication.class, args);
    }
}
