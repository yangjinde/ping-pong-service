import com.example.pingservice.controller.PingController
import com.example.pingservice.service.IPingService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import reactor.core.publisher.Mono
import spock.lang.Specification
import spock.lang.Title

/**
 * PingController Test
 *
 * @author yangjinde
 *
 * @date 2024/8/9
 */
@Title("PingController Test")
class PingControllerTest extends Specification {

    private def pingService = Mock(IPingService)
    private def pingController = new PingController(pingService)


    def "test PingControllerTest"() {
        def ok = Mono.just(ResponseEntity.status(HttpStatus.OK).body("World"))
        given:
        pingService.ping() >> ok
        when:
        def ping = pingController.ping()
        then:
        ping == ok
    }

}
