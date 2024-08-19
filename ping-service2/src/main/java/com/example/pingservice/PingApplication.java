package com.example.pingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Ping服务启动
 *
 * @author yangjinde
 * @date 2024/8/2
 */
@SpringBootApplication
@EnableScheduling
public class PingApplication {
    public static void main(String[] args) {
        SpringApplication.run(PingApplication.class, args);
    }
}
