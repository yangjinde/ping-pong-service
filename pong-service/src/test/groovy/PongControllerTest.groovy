import com.example.pongservice.controller.PongController
import com.example.pongservice.service.IPongService
import org.springframework.http.ResponseEntity
import reactor.core.publisher.Mono
import spock.lang.Specification
import spock.lang.Title

@Title("PongController Test")
class PongControllerTest extends Specification {

    private def pongService = Mock(IPongService)
    private def pongController = new PongController(pongService)

    def "test PongController"() {
        given:
        Mono<String> mono = Mono.just("Hello")
        Mono<ResponseEntity<String>> res = Mono.just(ResponseEntity.ok().body("World"))
        pongService.pong(mono) >> res
        when:
        def pong = pongController.pong(mono)
        then:
        pong.block().getBody() == "World"
    }

}
