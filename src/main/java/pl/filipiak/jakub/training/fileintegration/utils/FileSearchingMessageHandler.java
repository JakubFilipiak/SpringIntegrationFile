package pl.filipiak.jakub.training.fileintegration.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

import java.io.File;
import java.io.FileNotFoundException;

@Slf4j
public class FileSearchingMessageHandler implements MessageHandler {

    private final String configId;
    private final String pattern1;
    private final String pattern2;

    private MessagePublisher messagePublisher;

    public FileSearchingMessageHandler(String configId,
                                       String pattern1,
                                       String pattern2,
                                       MessagePublisher messagePublisher) {
        this.configId = configId;
        this.pattern1 = pattern1;
        this.pattern2 = pattern2;
        this.messagePublisher = messagePublisher;
    }

    @Override
    public void handleMessage(Message<?> message) {
        Object payload = message.getPayload();
        if (!(payload instanceof File)) return;
        File directory = (File) payload;
        PairOfFilesSearcher filesSearcher = new PairOfFilesSearcher(directory, pattern1, pattern2);
        try {
            filesSearcher.search();
            String result = assembleMessage(filesSearcher.getResultsStringRepresentation());
            log.info(result);
            messagePublisher.publishMessage(result);
        } catch (FileNotFoundException e) {
            log.warn(e.getMessage());
        } catch (MoreFilesThanExpectedException | MessagingException e) {
            log.error(e.getMessage());
        }
    }

    private String assembleMessage(String filesResult) {
        return configId + ", " + filesResult;
    }
}
