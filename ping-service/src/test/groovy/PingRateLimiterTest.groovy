

import com.example.pingservice.util.PingRateLimiter
import spock.lang.Specification
import spock.lang.Title

/**
 * PingRateLimiter Test
 *
 * @author yangjinde
 *
 * @date 2024/8/9
 */
@Title("PingRateLimiter Test")
class PingRateLimiterTest extends Specification {

    def "test 1 request in 1 second"() {
        when:
        def result = PingRateLimiter.checkRateLimit("data/" + UUID.randomUUID().toString() + ".lock") // 1次请求
        then:
        Boolean.TRUE == result
    }

    def "test 2 request in 1 second"() {
        when:
        PingRateLimiter.checkRateLimit() // 第1次请求
        def result = PingRateLimiter.checkRateLimit("data/" + UUID.randomUUID().toString() + ".lock") // 第2次请求
        then:
        Boolean.TRUE == result
    }

    def "test 3 request in 1 second"() {
        String file = "data/" + UUID.randomUUID().toString() + ".lock";
        when:
        PingRateLimiter.checkRateLimit(file) // 第1次请求
        PingRateLimiter.checkRateLimit(file) // 第2次请求
        def result = PingRateLimiter.checkRateLimit(file) // 第3次请求，应该被拒绝
        then:
        Boolean.FALSE == result
    }

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
