import com.example.pongservice.constant.Constant
import com.example.pongservice.util.PongRateLimiter
import spock.lang.Specification
import spock.lang.Title

@Title("PongRateLimiter Test")
class PongRateLimiterTest extends Specification {

    def "test 1 request in 1 second"() {
        when:
        def fileLock = PongRateLimiter.checkRateLimit("data/" + UUID.randomUUID().toString() + ".lock") // 1 request
        then:
        fileLock != null
    }

    def "test 2 request in 1 second"() {
        String file = "data/" + UUID.randomUUID().toString() + ".lock";
        when:
        def fileLock1 = PongRateLimiter.checkRateLimit(file) // 1 request
        PongRateLimiter.releaseFileLock(fileLock1)
        def fileLock2 = PongRateLimiter.checkRateLimit(file) // 2 request
        then:
        fileLock2 == null
    }

    def "test 2 request in 2 second"() {
        String file = "data/" + UUID.randomUUID().toString() + ".lock";
        when:
        def fileLock1 = PongRateLimiter.checkRateLimit(file) // 1 request
        PongRateLimiter.releaseFileLock(fileLock1);
        sleep(1500) // sleep 1.5 seconds
        def fileLock2 = PongRateLimiter.checkRateLimit(file) // 2 request
        then:
        fileLock2 != null
        cleanup:
        PongRateLimiter.releaseFileLock(fileLock2);
    }
}
