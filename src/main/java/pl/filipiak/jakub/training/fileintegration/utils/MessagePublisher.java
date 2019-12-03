package pl.filipiak.jakub.training.fileintegration.utils;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@EnableBinding(Source.class)
public class MessagePublisher {

    private Source source;

    public MessagePublisher(Source source) {
        this.source = source;
    }

    public void publishMessage(String content) {
        Message<String> message = MessageBuilder.withPayload(content).build();
        source.output().send(message);
    }
}
