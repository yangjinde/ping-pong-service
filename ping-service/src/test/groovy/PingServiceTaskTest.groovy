import com.example.pingservice.service.IPingService
import com.example.pingservice.task.PingServiceTask
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import reactor.core.publisher.Mono
import spock.lang.Specification
import spock.lang.Title

@Title("PingServiceTask Test")
class PingServiceTaskTest extends Specification{
    private def pingService = Mock(IPingService)
    private def pingServiceTask;

    def "test PingControllerTest"() {
        pingServiceTask = new PingServiceTask(pingService);
        def ok = Mono.just(ResponseEntity.status(HttpStatus.OK).body("World"))
        given:
        pingService.ping() >> ok
        when:
        pingServiceTask.runPingTask()
        then:
        noExceptionThrown()
    }
}
