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
        def result = PingRateLimiter.checkRateLimit("data/" + UUID.randomUUID().toString() + ".lock") // 1 request
        then:
        Boolean.TRUE == result
    }

    def "test 2 request in 1 second"() {
        String file = "data/" + UUID.randomUUID().toString() + ".lock";
        when:
        PingRateLimiter.checkRateLimit(file) // 1 request
        def result = PingRateLimiter.checkRateLimit(file) // 2 request
        then:
        Boolean.TRUE == result
    }

    def "test 3 request in 1 second"() {
        String file = "data/" + UUID.randomUUID().toString() + ".lock";
        when:
        PingRateLimiter.checkRateLimit(file) // 1 request
        PingRateLimiter.checkRateLimit(file) // 2 request
        def result = PingRateLimiter.checkRateLimit(file) // 3 request
        then:
        Boolean.FALSE == result
    }

    def "test 3 request in 2 second"() {
        String file = "data/" + UUID.randomUUID().toString() + ".lock";
        when:
        PingRateLimiter.checkRateLimit(file) // 1 request
        PingRateLimiter.checkRateLimit(file) // 2 request
        sleep(1300) // sleep 1.3 seconds
        def result = PingRateLimiter.checkRateLimit(file) // 3 request
        then:
        Boolean.TRUE == result
    }

    def "test checkRateLimit with out lockFilePath"() {
        when:
        def result = PingRateLimiter.checkRateLimit()
        then:
        Boolean.TRUE == result
    }
}
