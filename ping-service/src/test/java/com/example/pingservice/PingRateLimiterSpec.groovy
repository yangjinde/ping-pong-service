package com.example.pingservice

import com.example.pingservice.util.PingRateLimiter
import spock.lang.Specification

/**
 * Ping速率控制测试
 *
 * @author yangjinde
 *
 * @date 2024/8/9
 */
class PingRateLimiterSpec extends Specification {

    /**
     * 测试1秒内请求1次，正常返回true
     */
    def "test 1 request in 1 second"() {
        sleep(1200)//先睡眠1.2秒，排除干扰
        when:
        def result = PingRateLimiter.checkRateLimit() // 1次请求
        then:
        Boolean.TRUE == result
    }

    /**
     * 测试1秒内请求2次，正常返回true
     */
    def "test 2 request in 1 second"() {
        sleep(1200)//先睡眠1.2秒，排除干扰
        when:
        PingRateLimiter.checkRateLimit() // 第1次请求
        def result = PingRateLimiter.checkRateLimit() // 第2次请求
        then:
        Boolean.TRUE == result
    }

    /**
     * 测试1秒内请求3次，正常返回true
     */
    def "test 3 request in 1 second"() {
        sleep(1200)//先睡眠1.2秒，排除干扰
        when:
        PingRateLimiter.checkRateLimit() // 第1次请求
        PingRateLimiter.checkRateLimit() // 第2次请求
        def result = PingRateLimiter.checkRateLimit() // 第3次请求，应该被拒绝
        then:
        Boolean.FALSE == result
    }

    /**
     * 测试1秒内请求3次，正常返回true
     */
    def "test 3 request in 2 second"() {
        sleep(1200)//先睡眠1.2秒，排除干扰
        when:
        PingRateLimiter.checkRateLimit() // 第1次请求
        PingRateLimiter.checkRateLimit() // 第2次请求
        sleep(1200) // 等待1.3秒，确保超过1秒时间窗口
        def result = PingRateLimiter.checkRateLimit() // 第3次请求，应该被允许
        then:
        Boolean.TRUE == result
    }
}
