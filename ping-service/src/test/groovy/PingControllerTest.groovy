import com.example.pingservice.controller.PingController
import com.example.pingservice.dto.PingResDto
import com.example.pingservice.service.IPingService
import org.springframework.http.HttpStatus
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
        PingResDto pingRes = new PingResDto();
        pingRes.setStatus(HttpStatus.OK.value())
        pingRes.setBody("World");
        def res = Mono.just(pingRes)
        given:
        pingService.ping() >> res
        when:
        def result = pingController.ping()
        then:
        result.block().body == "World"
    }

}
