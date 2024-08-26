import com.example.pingservice.dto.PingResDto
import com.example.pingservice.service.impl.PingServiceImpl
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Specification
import spock.lang.Title

@Title("HandleResponse Test")
class HandleResponseTest extends Specification {

    private def pongServiceUrl = "http://localhost:8080/pong"
    private WebClient webClient
    private PingServiceImpl pingService

    def setup() {
        def webClientBuilder = Mock(WebClient.Builder)
        webClient = Mock(WebClient)
        webClientBuilder.baseUrl("http://localhost:8080") >> webClientBuilder
        webClientBuilder.build() >> webClient
        pingService = new PingServiceImpl(pongServiceUrl, webClientBuilder, "data/" + UUID.randomUUID().toString() + ".lock")
    }

    def "test handleResponse with 200 status"() {
        given:
        def mockResponse = Mock(ClientResponse)
        mockResponse.statusCode() >> HttpStatus.OK
        PingResDto pingRes = new PingResDto();
        pingRes.setStatus(HttpStatus.OK.value())
        pingRes.setBody("World")
        mockResponse.bodyToMono(PingResDto.class) >> Mono.just(pingRes)

        when:
        def result = pingService.handleResponse(mockResponse)

        then:
        StepVerifier.create(result)
                .expectNextMatches { pingResDto ->
                    pingResDto.status == HttpStatus.OK.value() && pingResDto.body == "World"
                }
                .verifyComplete()
    }

    def "test handleResponse with TOO_MANY_REQUESTS status"() {
        given:
        def mockResponse = Mock(ClientResponse)
        mockResponse.statusCode() >> HttpStatus.TOO_MANY_REQUESTS
        PingResDto pingRes = new PingResDto();
        pingRes.setStatus(HttpStatus.TOO_MANY_REQUESTS.value())
        pingRes.setErrorMsg(HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase())
        mockResponse.bodyToMono(PingResDto.class) >> Mono.just(pingRes)

        when:
        def result = pingService.handleResponse(mockResponse)

        then:
        StepVerifier.create(result)
                .expectNextMatches { pingResDto ->
                    pingResDto.status == HttpStatus.TOO_MANY_REQUESTS.value() && pingResDto.errorMsg == HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase()
                }
                .verifyComplete()
    }

    def "test handleResponse with unexpected status"() {
        given:
        def mockResponse = Mock(ClientResponse)
        mockResponse.statusCode() >> HttpStatus.INTERNAL_SERVER_ERROR
        PingResDto pingRes = new PingResDto();
        pingRes.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
        pingRes.setErrorMsg(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
        mockResponse.bodyToMono(PingResDto.class) >> Mono.just(pingRes)

        when:
        def result = pingService.handleResponse(mockResponse)

        then:
        StepVerifier.create(result)
                .expectNextMatches { pingResDto ->
                    pingResDto.status == HttpStatus.INTERNAL_SERVER_ERROR.value() && pingResDto.errorMsg == HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()
                }
                .verifyComplete()
    }
}
