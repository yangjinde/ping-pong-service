import com.example.pongservice.PongApplication
import com.example.pongservice.service.IPongService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification
import spock.lang.Title

@Title("PongApplication Test")
@SpringBootTest(classes = PongApplication.class, useMainMethod = SpringBootTest.UseMainMethod.ALWAYS)
class PongApplicationTest extends Specification {

    @Autowired
    private IPongService pongService

    def "test start application"() {
        expect:
        pongService
    }
}
