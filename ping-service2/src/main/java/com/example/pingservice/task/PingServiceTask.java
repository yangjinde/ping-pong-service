package com.example.pingservice.task;

import com.example.pingservice.service.IPingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import reactor.core.publisher.Mono;

/**
 * Ping定时任务
 *
 * @author yangjinde
 * @date 2024/8/7
 */
@Slf4j
@Configuration
@Profile("!UnitTest") //单元测试不启动定时任务，防止干扰
public class PingServiceTask {

    @Autowired
    private IPingService pingService;

    /**
     * 随机定时任务，1秒执行一次
     */
    @Scheduled(fixedRate = 1000)
    public void runPingTask() {
        Mono<ResponseEntity<String>> mono = pingService.ping();
        ResponseEntity<String> res = mono.block();
        //log.info("res:{}", res);
    }
}
