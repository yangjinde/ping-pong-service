package com.example.pingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Ping Server Start Class
 *
 * @author yangjinde
 * @date 2024/8/15
 */
@SpringBootApplication
@EnableScheduling
public class PingApplication {

    /**
     * Launches the Ping application
     *
     * @param args args
     */
    public static void main(String[] args) {
        SpringApplication.run(PingApplication.class, args);
    }
}
