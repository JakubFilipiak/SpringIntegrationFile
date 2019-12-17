package pl.filipiak.jakub.training.fileintegration.utils

import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import pl.filipiak.jakub.training.fileintegration.integration.MessagePublisher
import spock.lang.Specification

import java.nio.file.Path
import java.util.regex.Pattern

class FileSearchingMessageHandlerTest extends Specification {

    // Class to be tested
    private FileSearchingMessageHandler messageHandler

    // Dependencies
    private String configId
    private String pattern1
    private String pattern2
    private FileSearcherResultsValidator resultsValidator
    private MessagePublisher messagePublisher

    // Test data
    private Message<File> message

    def setup() {
        configId = "configId"
        pattern1 = "(.+)+(\\.(txt))"
        pattern2 = "(.+)+(\\.(csv))"
        resultsValidator = Stub(FileSearcherResultsValidator.class)
        messagePublisher = Mock(MessagePublisher.class)
        messageHandler = new FileSearchingMessageHandler(
                configId,
                pattern1,
                pattern2,
                resultsValidator,
                messagePublisher)
        File file = new File("target/test-resources")
        file.mkdir()
        message = MessageBuilder.withPayload(file).build()
    }

    def "Should correct publish message when searching result correct"() {
        given:
        resultsValidator.isCorrect(_ as Map<Pattern, List<Path>>) >> true

        when:
        messageHandler.handleMessage(message)

        then:
        1 * messagePublisher.publishWithPayload(_)
    }

    def "Should correct not publish message when searching results not correct"() {
        given:
        resultsValidator.isCorrect(_ as Map<Pattern, List<Path>>) >> false

        when:
        messageHandler.handleMessage(message)

        then:
        0 * messagePublisher.publishWithPayload(_)
    }

    def "Should do nothing when incorrect message type"() {
        given:
        Stub(FileSearcherResultsValidator.class)
        Mock(MessagePublisher.class)
        Message<String> incorrectMessage = MessageBuilder.withPayload("zxc").build()

        when:
        messageHandler.handleMessage(incorrectMessage)

        then:
        0 * resultsValidator.isCorrect(_)
        0 * messagePublisher.publishWithPayload(_)
    }
}
