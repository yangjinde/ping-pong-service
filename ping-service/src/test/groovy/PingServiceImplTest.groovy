import com.example.pingservice.PingApplication
import com.example.pingservice.service.IPingService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import spock.lang.Specification

// 设置spring.profiles.active=UnitTest，不启动定时任务防止干扰
@SpringBootTest(classes = PingApplication.class, useMainMethod = SpringBootTest.UseMainMethod.ALWAYS, properties = "spring.profiles.active=UnitTest")
class PingServiceImplTest extends Specification {

    @Autowired
    private IPingService pingService;

    /**
     * 1秒内ping 1次，正常返回World
     */
    def "test 1 ping in 1 second"() {
        sleep(1200)//先睡眠1.2秒，排除干扰
        when:
        def res = pingService.ping().block().body
        then:
        res == "World"
    }

    /**
     * 1秒内连续ping 2次，第1次请求正常返回World，第2次请求429状态
     */
    def "test 2 ping in 1 second"() {
        sleep(1200)//先睡眠1.2秒，排除干扰
        when:
        def res1 = pingService.ping().block().body
        def res2StatusCode = pingService.ping().block().statusCode
        then:
        res1 == "World" && res2StatusCode == HttpStatus.TOO_MANY_REQUESTS
    }

    /**
     * 1秒内连续ping 3次，第1个请求正常返回World，第2个请求返回429状态，第3个请求发不出去
     */
    def "test 3 ping in 1 second"() {
        sleep(1200)//先睡眠1.2秒，排除干扰
        when:
        def res1 = pingService.ping().block().body
        def res2StatusCode = pingService.ping().block().statusCode
        def res3StatusCode = pingService.ping().block().statusCode
        then:
        res1 == "World" && res2StatusCode == HttpStatus.TOO_MANY_REQUESTS && res3StatusCode == HttpStatus.TOO_MANY_REQUESTS
    }
}
