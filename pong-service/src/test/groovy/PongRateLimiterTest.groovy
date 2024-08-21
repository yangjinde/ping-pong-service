package com.example.pongservice

import com.example.pongservice.util.PongRateLimiter
import spock.lang.Specification

/**
 * Ping速率控制测试
 *
 * @author yangjinde
 *
 * @date 2024/8/9
 */
class PongRateLimiterTest extends Specification {

    /**
     * 测试1秒内请求1次，正常返回true
     */
    def "test 1 request in 1 second"() {
        when:
        def res = PongRateLimiter.checkRateLimit("data/" + UUID.randomUUID().toString() + ".lock") // 1次请求
        then:
        Boolean.TRUE == res
    }

    /**
     * 测试1秒内只能有1个请求通过，连续请求2次，第1次，正常返回true，第2次受到限制返回false
     */
    def "test 2 request in 1 second"() {
        String file = "data/" + UUID.randomUUID().toString() + ".lock"
        when:
        def res1 = PongRateLimiter.checkRateLimit(file) // 第1次请求
        def res2 = PongRateLimiter.checkRateLimit(file) // 第2次请求
        then:
        res1 == Boolean.TRUE && res2 == Boolean.FALSE
    }
}
