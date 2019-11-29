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
import pl.filipiak.jakub.training.fileintegration.config.properties.Dir3ConfigProperties;
import pl.filipiak.jakub.training.fileintegration.utils.MessagePublisher;

import java.io.File;

@Configuration
@ConditionalOnProperty(
        prefix = "user-variables.integration.directories-scan-configs.config3",
        name = "enabled",
        havingValue = "true")
@EnableConfigurationProperties(Dir3ConfigProperties.class)
public class Dir3FileIntegrationConfig extends AbstractFileIntegrationConfig {

    private MessagePublisher messagePublisher;

    public Dir3FileIntegrationConfig(Dir3ConfigProperties properties, MessagePublisher messagePublisher) {
        super("3",
                "config3",
                properties.getStorageDirectory(),
                properties.getFile1AcceptPattern(),
                properties.getFile2AcceptPattern(),
                properties.getMetadataKeyPrefix(),
                properties.isDirectoriesValidationEnabled(),
                properties.getDirectoriesAcceptPattern());
        this.messagePublisher = messagePublisher;
    }

    @Bean
    public MessageChannel directory3Channel() {
        return super.createMessageChannel();
    }

    @Bean
    @InboundChannelAdapter(value = "directory3Channel", poller = @Poller(fixedDelay = INTERVAL_IN_MILLIS))
    public MessageSource<File> directory3FileReadingMessageSource() {
        return super.createFileReadingMessageSource(directory3MetadataStore());
    }

    @Bean
    public ConcurrentMetadataStore directory3MetadataStore() {
        return super.createMetadataStore();
    }

    @Bean
    @ServiceActivator(inputChannel = "directory3Channel")
    public MessageHandler directory3MessageHandler() {
        return super.createMessageHandler(messagePublisher);
    }
}
