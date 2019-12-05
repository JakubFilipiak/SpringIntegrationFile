package pl.filipiak.jakub.training.fileintegration.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@EnableBinding(Source.class)
public class MessagePublisher {

    private Source source;

    public MessagePublisher(Source source) {
        this.source = source;
    }

    public void publishWithPayload(PathsContainingMessagePayload payload) {
        buildMessage(payload).ifPresent(message -> {
            if (source.output().send(message))
                log.info("Sent message with payload: " + message.getPayload());
        });
    }

    private Optional<Message<PathsContainingMessagePayload>> buildMessage(PathsContainingMessagePayload payload) {
        if (payload == null) return Optional.empty();
        return Optional.of(MessageBuilder.withPayload(payload).build());
    }
}
