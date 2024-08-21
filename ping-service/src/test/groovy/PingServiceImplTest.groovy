import com.example.pingservice.service.impl.PingServiceImpl
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import spock.lang.Specification
import spock.lang.Title

@Title("PingService单测")
class PingServiceImplTest extends Specification {

    private def pongServiceUrl = "http://localhost:8080/pong"
    private WebClient webClient
    private PingServiceImpl pingService

    /**
     * 当前文件的单测执行前会执行一次
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


    /**
     * 1秒内ping 1次，正常返回World
     */
    def "test 1 ping in 1 second"() {
        given:
        def mockUriSpec = Mock(WebClient.RequestBodyUriSpec)
        def mockSpec = Mock(WebClient.RequestBodySpec)
        def mockHeaderSpec = Mock(WebClient.RequestHeadersSpec)
        webClient.post() >> mockUriSpec
        mockUriSpec.uri(pongServiceUrl) >> mockSpec
        mockSpec.bodyValue("Hello") >> mockHeaderSpec
        def resp = Mono.just(ResponseEntity.status(HttpStatus.OK).body("World"))
        1 * mockHeaderSpec.exchangeToMono(_) >> resp
        resp.doOnError(_) >> resp
        when:
        def res = pingService.ping().block().body
        then:
        res == "World"
    }

    /**
     * 1秒内连续ping 2次，第1次请求正常返回World，第2次请求429状态
     */
    def "test 2 ping in 1 second"() {
        given:
        def mockUriSpec = Mock(WebClient.RequestBodyUriSpec)
        def mockSpec = Mock(WebClient.RequestBodySpec)
        def mockHeaderSpec = Mock(WebClient.RequestHeadersSpec)
        def resp = Mono.just(ResponseEntity.status(HttpStatus.OK).body("World"))
        def r2 = Mono.just(ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Too many requests in one second"))
        webClient.post() >> mockUriSpec
        mockUriSpec.uri(pongServiceUrl) >> mockSpec
        mockSpec.bodyValue("Hello") >> mockHeaderSpec
        when:
        1 * mockHeaderSpec.exchangeToMono(_) >> resp
        resp.doOnError(_) >> resp
        def res1 = pingService.ping().block().body

        1 * mockHeaderSpec.exchangeToMono(_) >> r2
        r2.doOnError(_) >> r2
        def res2StatusCode = pingService.ping().block().statusCode
        then:
        res1 == "World" && res2StatusCode == HttpStatus.TOO_MANY_REQUESTS
    }

    /**
     * 1秒内连续ping 3次，第1个请求正常返回World，第2个请求返回429状态，第3个请求发不出去
     */
    def "test 3 ping in 1 second"() {
        given:
        def mockUriSpec = Mock(WebClient.RequestBodyUriSpec)
        def mockSpec = Mock(WebClient.RequestBodySpec)
        def mockHeaderSpec = Mock(WebClient.RequestHeadersSpec)
        def resp = Mono.just(ResponseEntity.status(HttpStatus.OK).body("World"))
        def r2 = Mono.just(ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Too many requests in one second"))
        //def r3 = Mono.just(ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Too many requests in one second"))
        webClient.post() >> mockUriSpec
        mockUriSpec.uri(pongServiceUrl) >> mockSpec
        mockSpec.bodyValue("Hello") >> mockHeaderSpec
        when:
        1 * mockHeaderSpec.exchangeToMono(_) >> resp
        resp.doOnError(_) >> resp
        def res1 = pingService.ping().block().body
        1 * mockHeaderSpec.exchangeToMono(_) >> r2
        r2.doOnError(_) >> r2
        def res2StatusCode = pingService.ping().block().statusCode
        //1 * mockHeaderSpec.exchangeToMono(_) >> r3
        //r3.doOnError(_) >> r3
        def res3StatusCode = pingService.ping().block().statusCode
        then:
        res1 == "World" && res2StatusCode == HttpStatus.TOO_MANY_REQUESTS && res3StatusCode == HttpStatus.TOO_MANY_REQUESTS
    }
}
