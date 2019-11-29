package pl.filipiak.jakub.training.fileintegration.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.metadata.ConcurrentMetadataStore;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import pl.filipiak.jakub.training.fileintegration.config.helpers.AbstractFileIntegrationConfig;
import pl.filipiak.jakub.training.fileintegration.config.properties.Dir4ConfigProperties;
import pl.filipiak.jakub.training.fileintegration.utils.MessagePublisher;

import java.io.File;

@Configuration
@ConditionalOnProperty(
        prefix = "user-variables.integration.directories-scan-configs.config3",
        name = "enabled",
        havingValue = "true")
@EnableConfigurationProperties(Dir4ConfigProperties.class)
public class Dir4FileIntegrationConfig extends AbstractFileIntegrationConfig {

    private MessagePublisher messagePublisher;

    public Dir4FileIntegrationConfig(Dir4ConfigProperties properties, MessagePublisher messagePublisher) {
        super("4",
                "config4",
                properties.getStorageDirectory(),
                properties.getFile1AcceptPattern(),
                properties.getFile2AcceptPattern(),
                properties.getMetadataKeyPrefix(),
                properties.isDirectoriesValidationEnabled(),
                properties.getDirectoriesAcceptPattern());
        this.messagePublisher = messagePublisher;
    }

    @Bean
    public MessageChannel directory4Channel() {
        return super.createMessageChannel();
    }

    @Bean
    @InboundChannelAdapter(value = "directory4Channel", poller = @Poller(fixedDelay = INTERVAL_IN_MILLIS))
    public MessageSource<File> directory4FileReadingMessageSource() {
        return super.createFileReadingMessageSource(directory4MetadataStore());
    }

    @Bean
    public ConcurrentMetadataStore directory4MetadataStore() {
        return super.createMetadataStore();
    }

    @Bean
    @ServiceActivator(inputChannel = "directory4Channel")
    public MessageHandler directory4MessageHandler() {
        return super.createMessageHandler(messagePublisher);
    }
}
