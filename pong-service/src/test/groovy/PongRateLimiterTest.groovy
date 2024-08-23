import com.example.pongservice.constant.Constant
import com.example.pongservice.util.PongRateLimiter
import spock.lang.Specification
import spock.lang.Title

@Title("PongRateLimiter Test")
class PongRateLimiterTest extends Specification {

    def "test 1 request in 1 second"() {
        when:
        def result = PongRateLimiter.checkRateLimit("data/" + UUID.randomUUID().toString() + ".lock") // 1 request
        then:
        Boolean.TRUE == result
    }

    def "test 2 request in 1 second"() {
        String file = "data/" + UUID.randomUUID().toString() + ".lock";
        when:
        PongRateLimiter.checkRateLimit(file) // 1 request
        def result = PongRateLimiter.checkRateLimit(file) // 2 request
        then:
        Boolean.FALSE == result
    }

    def "test 2 request in 2 second"() {
        String file = "data/" + UUID.randomUUID().toString() + ".lock";
        when:
        PongRateLimiter.checkRateLimit(file) // 1 request
        sleep(1500) // sleep 1.5 seconds
        def result = PongRateLimiter.checkRateLimit(file) // 2 request
        then:
        Boolean.TRUE == result
    }

    def "test Constant "() {
        when:
        def oneSecond = Constant.ONE_SECOND
        def maxReqNum = Constant.MAX_REQ_NUM
        def dateFormat  = Constant.DATE_FORMAT
        then:
        oneSecond == 1
        maxReqNum == 1
        dateFormat == "yyyy-MM-dd HH:mm:ss"
    }
}
