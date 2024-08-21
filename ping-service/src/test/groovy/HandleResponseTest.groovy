import com.example.pingservice.service.impl.PingServiceImpl
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Specification

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
        mockResponse.bodyToMono(String.class) >> Mono.just("Success")

        when:
        def result = pingService.handleResponse(mockResponse)

        then:
        StepVerifier.create(result)
                .expectNextMatches { responseEntity ->
                    responseEntity.statusCode == HttpStatus.OK && responseEntity.body == "Success"
                }
                .verifyComplete()
    }

    def "test handleResponse with TOO_MANY_REQUESTS status"() {
        given:
        def mockResponse = Mock(ClientResponse)
        mockResponse.statusCode() >> HttpStatus.TOO_MANY_REQUESTS
        mockResponse.bodyToMono(String.class) >> Mono.just("Throttled")

        when:
        def result = pingService.handleResponse(mockResponse)

        then:
        StepVerifier.create(result)
                .expectNextMatches { responseEntity ->
                    responseEntity.statusCode == HttpStatus.TOO_MANY_REQUESTS && responseEntity.body == "Throttled"
                }
                .verifyComplete()
    }

    def "test handleResponse with unexpected status"() {
        given:
        def mockResponse = Mock(ClientResponse)
        mockResponse.statusCode() >> HttpStatus.INTERNAL_SERVER_ERROR
        mockResponse.bodyToMono(String.class) >> Mono.just("Error")

        when:
        def result = pingService.handleResponse(mockResponse)

        then:
        StepVerifier.create(result)
                .expectNextMatches { responseEntity ->
                    responseEntity.statusCode == HttpStatus.INTERNAL_SERVER_ERROR && responseEntity.body == "Error"
                }
                .verifyComplete()
    }
}
