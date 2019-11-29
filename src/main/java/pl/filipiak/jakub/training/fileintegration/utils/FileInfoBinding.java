package pl.filipiak.jakub.training.fileintegration.utils;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

@Component
public interface FileInfoBinding {

    @Output("fileInfoChannel")
    MessageChannel fileInfo();
}
