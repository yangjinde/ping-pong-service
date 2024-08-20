package com.example.pongservice

import com.example.pongservice.service.IPongService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTestContextBootstrapper
import org.springframework.http.HttpStatus
import org.springframework.test.context.BootstrapWith
import reactor.core.publisher.Mono
import spock.lang.Specification

// 设置spring.profiles.active=UnitTest，不启动定时任务防止干扰
@BootstrapWith(SpringBootTestContextBootstrapper)
@AutoConfigureWebTestClient
@SpringBootTest(classes = PongApplication.class, useMainMethod = SpringBootTest.UseMainMethod.ALWAYS, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.profiles.active=UnitTest")
class PongServiceImplSpec extends Specification {

    @Autowired
    private IPongService pongService;

    /**
     * 1秒内pong 1次，正常返回World
     */
    def "test 1 pong in 1 second"() {
        sleep(1200)//先睡眠1.2秒，排除干扰
        Mono<String> monoReq = Mono.just("Hello");
        when:
        def res = pongService.pong(monoReq).block().body
        then:
        res == "World"
    }

    /**
     * 1秒内连续pong 2次，第1次请求正常返回World，第2次请求429状态
     */
    def "test 2 pong in 1 second"() {
        sleep(1200)//先睡眠1.2秒，排除干扰
        Mono<String> monoReq = Mono.just("Hello");
        when:
        def res1 = pongService.pong(monoReq).block().body
        def res2StatusCode = pongService.pong(monoReq).block().statusCode
        then:
        res1 == "World" && res2StatusCode == HttpStatus.TOO_MANY_REQUESTS
    }
}
