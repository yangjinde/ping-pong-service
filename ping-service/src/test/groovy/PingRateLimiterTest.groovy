

import com.example.pingservice.util.PingRateLimiter
import spock.lang.Specification

/**
 * Ping速率控制测试
 *
 * @author yangjinde
 *
 * @date 2024/8/9
 */
class PingRateLimiterTest extends Specification {

    /**
     * 测试1秒内请求1次，正常返回true
     */
    def "test 1 request in 1 second"() {
        when:
        def result = PingRateLimiter.checkRateLimit("data/" + UUID.randomUUID().toString() + ".lock") // 1次请求
        then:
        Boolean.TRUE == result
    }

    /**
     * 测试1秒内请求2次，正常返回true
     */
    def "test 2 request in 1 second"() {
        when:
        PingRateLimiter.checkRateLimit() // 第1次请求
        def result = PingRateLimiter.checkRateLimit("data/" + UUID.randomUUID().toString() + ".lock") // 第2次请求
        then:
        Boolean.TRUE == result
    }

    /**
     * 测试1秒内请求3次，正常返回true
     */
    def "test 3 request in 1 second"() {
        String file = "data/" + UUID.randomUUID().toString() + ".lock";
        when:
        PingRateLimiter.checkRateLimit(file) // 第1次请求
        PingRateLimiter.checkRateLimit(file) // 第2次请求
        def result = PingRateLimiter.checkRateLimit(file) // 第3次请求，应该被拒绝
        then:
        Boolean.FALSE == result
    }

    /**
     * 测试1秒内请求3次，正常返回true
     */
    def "test 3 request in 2 second"() {
        String file = "data/" + UUID.randomUUID().toString() + ".lock";
        when:
        PingRateLimiter.checkRateLimit(file) // 第1次请求
        PingRateLimiter.checkRateLimit(file) // 第2次请求
        sleep(1200) // 等待1.3秒，确保超过1秒时间窗口
        def result = PingRateLimiter.checkRateLimit(file) // 第3次请求，应该被允许
        then:
        Boolean.TRUE == result
    }
}
