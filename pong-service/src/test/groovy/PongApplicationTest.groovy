import com.example.pongservice.PongApplication
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest(classes = PongApplication.class, useMainMethod = SpringBootTest.UseMainMethod.ALWAYS)
class PongApplicationTest extends Specification {

    def "test start application"() {

    }
}
