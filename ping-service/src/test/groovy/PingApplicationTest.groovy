import com.example.pingservice.PingApplication
import com.example.pingservice.task.PingServiceTask
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification
import spock.lang.Title

@Title("PingApplication Test")
@SpringBootTest(classes = PingApplication.class, useMainMethod = SpringBootTest.UseMainMethod.ALWAYS)
class PingApplicationTest extends Specification {

    @Autowired
    private PingServiceTask pingServiceTask;

    def "test start application"() {
        expect:
        pingServiceTask
    }

}
