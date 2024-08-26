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
        def fileLock = PingRateLimiter.checkRateLimit("data/" + UUID.randomUUID().toString() + ".lock") // 1 request
        then:
        fileLock != null
        cleanup:
        PingRateLimiter.releaseFileLock(fileLock)
    }

    def "test 2 request in 1 second"() {
        String file = "data/" + UUID.randomUUID().toString() + ".lock";
        when:
        def fileLock1 = PingRateLimiter.checkRateLimit(file) // 1 request
        PingRateLimiter.releaseFileLock(fileLock1);
        def fileLock2 = PingRateLimiter.checkRateLimit(file) // 2 request
        then:
        fileLock2 != null
        cleanup:
        PingRateLimiter.releaseFileLock(fileLock2)
    }

    def "test 3 request in 1 second"() {
        String file = "data/" + UUID.randomUUID().toString() + ".lock";
        when:
        def fileLock1 = PingRateLimiter.checkRateLimit(file) // 1 request
        PingRateLimiter.releaseFileLock(fileLock1);
        def fileLock2 = PingRateLimiter.checkRateLimit(file) // 2 request
        PingRateLimiter.releaseFileLock(fileLock2);
        def fileLock3 = PingRateLimiter.checkRateLimit(file) // 3 request
        then:
        fileLock3 == null
    }

    def "test 3 request in 2 second"() {
        String file = "data/" + UUID.randomUUID().toString() + ".lock";
        when:
        def fileLock1 = PingRateLimiter.checkRateLimit(file) // 1 request
        PingRateLimiter.releaseFileLock(fileLock1);
        def fileLock2 = PingRateLimiter.checkRateLimit(file) // 2 request
        PingRateLimiter.releaseFileLock(fileLock2);
        sleep(1300) // sleep 1.3 seconds
        def fileLock3 = PingRateLimiter.checkRateLimit(file) // 3 request
        then:
        fileLock3 != null
        cleanup:
        PingRateLimiter.releaseFileLock(fileLock3)
    }

    def "test checkRateLimit with out lockFilePath"() {
        when:
        def fileLock = PingRateLimiter.checkRateLimit()
        then:
        fileLock != null
        cleanup:
        PingRateLimiter.releaseFileLock(fileLock)
    }
}
