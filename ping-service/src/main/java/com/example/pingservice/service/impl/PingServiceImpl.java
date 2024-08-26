package com.example.pingservice.service.impl;

import com.example.pingservice.constant.Constant;
import com.example.pingservice.dto.PingResDto;
import com.example.pingservice.logger.MyLogger;
import com.example.pingservice.service.IPingService;
import com.example.pingservice.util.PingRateLimiter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.channels.FileLock;

/**
 * PingServiceImpl
 *
 * @author yangjinde
 * @date 2024/8/16
 */
@Service
public class PingServiceImpl implements IPingService {

    @Value("${pong.service.url}")
    private final String pongServiceUrl;

    private final WebClient webClient;
    private final String lockFilePath;

    public PingServiceImpl(@Value("${pong.service.url}") String pongServiceUrl, WebClient.Builder webClientBuilder,
                           @Value("${ping.service.lockfile}") String lockFilePath) {
        this.pongServiceUrl = pongServiceUrl;
        this.webClient = webClientBuilder.build();
        this.lockFilePath = lockFilePath;
    }

    /**
     * run Ping
     *
     * @return Ping Result
     */
    @Override
    public Mono<PingResDto> ping() {
        //Rate control: at most 2 requests are allowed to pass per second.
        FileLock fileLock = PingRateLimiter.checkRateLimit(lockFilePath);
        try {
            if (null == fileLock) {
                MyLogger.error("Request not send as being 'rate limited'.");
                PingResDto pingRes = new PingResDto();
                pingRes.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                pingRes.setErrorMsg("Request not send as being 'rate limited'.");
                return Mono.just(pingRes);
            }
            return doPing();
        } catch (Exception e) {
            MyLogger.error(e.getMessage());
            return Mono.empty(); // return a empty Mono
        } finally {
            PingRateLimiter.releaseFileLock(fileLock);
        }
    }

    /**
     * do Ping
     *
     * @return Ping Result
     */
    private Mono<PingResDto> doPing() {
        MyLogger.info("Ping:Hello");
        return webClient.post()
                .uri(pongServiceUrl)
                .bodyValue(Constant.SAY_CONTENT)
                .exchangeToMono(this::handleResponse)
                .doOnError(IOException.class, e -> {
                    MyLogger.info("Rate limited, request not sent");
                });
    }

    /**
     * handle Pong Response
     *
     * @param response response
     * @return Mono<String>
     */
    public Mono<PingResDto> handleResponse(ClientResponse response) {
        return response.bodyToMono(PingResDto.class)
                .flatMap(body -> {
                    PingResDto pingRes = new PingResDto();
                    pingRes.setStatus(response.statusCode().value());
                    if (response.statusCode().is2xxSuccessful()) {
                        MyLogger.info("Pong:" + body.getBody());
                        pingRes.setBody(body.getBody());
                    } else if (response.statusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                        MyLogger.error("Pong:" + response.statusCode());
                        pingRes.setErrorMsg(body.getErrorMsg());
                    } else {
                        MyLogger.error("Received unexpected response: " + response.statusCode());
                        pingRes.setErrorMsg(body.getErrorMsg());
                    }
                    return Mono.just(pingRes);
                });
    }
}
