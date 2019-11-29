package pl.filipiak.jakub.training.fileintegration.utils;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class MessagePublisher {

    private MessageChannel channel;

    public MessagePublisher(FileInfoBinding binding) {
        this.channel = binding.fileInfo();
    }

    public void publishMessage(String content) {
        Message<String> message = MessageBuilder.withPayload(content).build();
        this.channel.send(message);
    }
}
