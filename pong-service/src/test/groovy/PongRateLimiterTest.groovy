import com.example.pongservice.util.PongRateLimiter
import spock.lang.Specification
import spock.lang.Title

@Title("PongRateLimiter Test")
class PongRateLimiterTest extends Specification {

    def "test 1 request in 1 second"() {
        when:
        def res = PongRateLimiter.checkRateLimit("data/" + UUID.randomUUID().toString() + ".lock") // 1 request
        then:
        Boolean.TRUE == res
    }

    def "test 2 request in 1 second"() {
        String file = "data/" + UUID.randomUUID().toString() + ".lock"
        when:
        def res1 = PongRateLimiter.checkRateLimit(file) // 1 request
        def res2 = PongRateLimiter.checkRateLimit(file) // 2 request
        then:
        res1 == Boolean.TRUE && res2 == Boolean.FALSE
    }
}
