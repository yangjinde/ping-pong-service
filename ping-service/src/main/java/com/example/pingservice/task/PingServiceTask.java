package com.example.pingservice.task;

import com.example.pingservice.logger.MyLogger;
import com.example.pingservice.service.IPingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Ping定时任务
 *
 * @author yangjinde
 * @date 2024/8/16
 */
@Component
public class PingServiceTask {

    @Autowired
    private IPingService pingService;

    /**
     * 随机定时任务，1秒执行一次
     */
    @Scheduled(fixedRate = 1000)
    public void runPingTask() {
        Mono<ResponseEntity<String>> pingRes = pingService.ping();
        MyLogger.info("Ping Result:" + pingRes.block().getBody());
    }
}
