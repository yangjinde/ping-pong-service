import com.example.pongservice.constant.Constant
import com.example.pongservice.logger.MyLogger
import spock.lang.Specification
import spock.lang.Title

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@Title("MyLogger Test")
class MyLoggerTest extends Specification {

    def "test createFileIfNotExists"() {
        String logFilePath = System.getProperty("user.home") + "/temp/" + UUID.randomUUID().toString() + "/ping.log"
        MyLogger.initLogFile(logFilePath);
        File logFile = new File(logFilePath);
        expect:
        logFile.exists()
        logFile.delete()
    }

    def "test log info"() {
        given:
        String logPath = Constant.LOG_PATH
        Path logFilePath = Paths.get(logPath)

        when:
        MyLogger.info("info")

        then:
        Files.exists(logFilePath)
        Files.readString(logFilePath).contains("info")
    }

    def "test warn info"() {
        given:
        String logPath = Constant.LOG_PATH
        Path logFilePath = Paths.get(logPath)

        when:
        MyLogger.warn("warn")

        then:
        Files.exists(logFilePath)
        Files.readString(logFilePath).contains("warn")
    }

    def "test error info"() {
        given:
        String logPath = new Constant().LOG_PATH
        Path logFilePath = Paths.get(logPath)

        when:
        MyLogger.error("error")

        then:
        Files.exists(logFilePath)
        Files.readString(logFilePath).contains("error")
    }

    def "test initLogFile fail"() {
        when:
        boolean  result = MyLogger.initLogFile("xxx")

        then:
        Boolean.FALSE == result
    }

    def "test initLogFile with no logPath"() {
        when:
        boolean  result = MyLogger.initLogFile(null)

        then:
        Boolean.TRUE == result
    }

    def "test constructor"() {
        when:
        MyLogger.class.getConstructor().newInstance()

        then:
        noExceptionThrown()
    }
}
