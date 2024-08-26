import com.example.pingservice.dto.PingResDto
import com.example.pingservice.service.impl.PingServiceImpl
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import spock.lang.Specification
import spock.lang.Title

@Title("PingServiceImpl Test")
class PingServiceImplTest extends Specification {

    private def pongServiceUrl = "http://localhost:8080/pong"
    private WebClient webClient
    private PingServiceImpl pingService

    /**
     * The single test of the current file will be executed once before execution
     * @return
     */
    // https://spockframework.org/spock/docs/2.3/spock_primer.html
    def setup() {
        def webClientBuilder = Mock(WebClient.Builder)
        webClient = Mock(WebClient)
        webClientBuilder.baseUrl("http://localhost:8080") >> webClientBuilder
        webClientBuilder.build() >> webClient
        pingService = new PingServiceImpl("http://localhost:8080/pong", webClientBuilder, "data/" + UUID.randomUUID().toString() + ".lock")
    }


    def "test 1 ping in 1 second"() {
        given:
        def mockUriSpec = Mock(WebClient.RequestBodyUriSpec)
        def mockSpec = Mock(WebClient.RequestBodySpec)
        def mockHeaderSpec = Mock(WebClient.RequestHeadersSpec)
        webClient.post() >> mockUriSpec
        mockUriSpec.uri(pongServiceUrl) >> mockSpec
        mockSpec.bodyValue("Hello") >> mockHeaderSpec

        PingResDto res1Just = new PingResDto();
        res1Just.setStatus(HttpStatus.OK.value())
        res1Just.setBody("World")
        def resp = Mono.just(res1Just)

        1 * mockHeaderSpec.exchangeToMono(_) >> resp
        resp.doOnError(_) >> resp
        when:
        def res = pingService.ping().block().body
        then:
        res == "World"
    }

    def "test 2 ping in 1 second"() {
        given:
        def mockUriSpec = Mock(WebClient.RequestBodyUriSpec)
        def mockSpec = Mock(WebClient.RequestBodySpec)
        def mockHeaderSpec = Mock(WebClient.RequestHeadersSpec)

        PingResDto res1Just = new PingResDto();
        res1Just.setStatus(HttpStatus.OK.value())
        res1Just.setBody("World")
        def resp = Mono.just(res1Just)

        PingResDto res2Just = new PingResDto();
        res2Just.setStatus(HttpStatus.TOO_MANY_REQUESTS.value())
        res2Just.setErrorMsg(HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase())
        def r2 = Mono.just(res2Just)

        webClient.post() >> mockUriSpec
        mockUriSpec.uri(pongServiceUrl) >> mockSpec
        mockSpec.bodyValue("Hello") >> mockHeaderSpec
        when:
        1 * mockHeaderSpec.exchangeToMono(_) >> resp
        resp.doOnError(_) >> resp
        def res1 = pingService.ping().block().body

        1 * mockHeaderSpec.exchangeToMono(_) >> r2
        r2.doOnError(_) >> r2
        def res2StatusCode = pingService.ping().block().status
        then:
        res1 == "World" && res2StatusCode == HttpStatus.TOO_MANY_REQUESTS.value()
    }

    def "test 3 ping in 1 second"() {
        given:
        def mockUriSpec = Mock(WebClient.RequestBodyUriSpec)
        def mockSpec = Mock(WebClient.RequestBodySpec)
        def mockHeaderSpec = Mock(WebClient.RequestHeadersSpec)

        PingResDto res1Just = new PingResDto();
        res1Just.setStatus(HttpStatus.OK.value())
        res1Just.setBody("World")
        def resp = Mono.just(res1Just)

        PingResDto res2Just = new PingResDto();
        res2Just.setStatus(HttpStatus.TOO_MANY_REQUESTS.value())
        res2Just.setErrorMsg(HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase())
        def r2 = Mono.just(res2Just)

        //def r3 = Mono.just(ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Too many requests in one second"))
        webClient.post() >> mockUriSpec
        mockUriSpec.uri(pongServiceUrl) >> mockSpec
        mockSpec.bodyValue("Hello") >> mockHeaderSpec
        when:
        1 * mockHeaderSpec.exchangeToMono(_) >> resp
        resp.doOnError(_) >> resp
        def res1 = pingService.ping().block().body
        println res1
        1 * mockHeaderSpec.exchangeToMono(_) >> r2
        r2.doOnError(_) >> r2
        def res2StatusCode = pingService.ping().block().status
        //1 * mockHeaderSpec.exchangeToMono(_) >> r3
        //r3.doOnError(_) >> r3
        def res3StatusCode = pingService.ping().block().status
        then:
        res1 == "World" && res2StatusCode == HttpStatus.TOO_MANY_REQUESTS.value() && res3StatusCode == HttpStatus.TOO_MANY_REQUESTS.value()
    }
}
