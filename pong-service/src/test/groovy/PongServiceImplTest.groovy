import com.example.pongservice.dto.PongResDto
import com.example.pongservice.service.impl.PongServiceImpl
import org.springframework.http.HttpStatus
import reactor.core.publisher.Mono
import spock.lang.Specification
import spock.lang.Title

@Title("PongServiceImpl Test")
class PongServiceImplTest extends Specification {

    private PongServiceImpl pongService

    /**
     * The single test of the current file will be executed once before execution
     * @return
     */
    // https://spockframework.org/spock/docs/2.3/spock_primer.html
    def setup() {
        pongService = new PongServiceImpl("data/" + UUID.randomUUID().toString() + ".lock")
    }

    def "test 1 pong in 1 second"() {
        Mono<String> monoReq = Mono.just("Hello");
        when:
        def res = pongService.pong(monoReq).block().body
        then:
        res == "World"
    }

    def "test 2 pong in 1 second"() {
        Mono<String> monoReq = Mono.just("Hello");
        when:
        def res1 = pongService.pong(monoReq).block().body
        def res2StatusCode = pongService.pong(monoReq).block().status
        then:
        res1 == "World" && res2StatusCode == HttpStatus.TOO_MANY_REQUESTS.value()
    }
}
