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
import pl.filipiak.jakub.training.fileintegration.config.properties.Dir1ConfigProperties;
import pl.filipiak.jakub.training.fileintegration.utils.MessagePublisher;

import java.io.File;

@Configuration
@ConditionalOnProperty(
        prefix = "user-variables.integration.directories-scan-configs.config1",
        name = "enabled",
        havingValue = "true")
@EnableConfigurationProperties(Dir1ConfigProperties.class)
public class Dir1FileIntegrationConfig extends AbstractFileIntegrationConfig {

    private MessagePublisher messagePublisher;

    public Dir1FileIntegrationConfig(Dir1ConfigProperties properties, MessagePublisher messagePublisher) {
        super("1",
                "config1",
                properties.getStorageDirectory(),
                properties.getFile1AcceptPattern(),
                properties.getFile2AcceptPattern(),
                properties.getMetadataKeyPrefix(),
                properties.isDirectoriesValidationEnabled(),
                properties.getDirectoriesAcceptPattern());
        this.messagePublisher = messagePublisher;
    }

    @Bean
    public MessageChannel directory1Channel() {
        return super.createMessageChannel();
    }

    @Bean
    @InboundChannelAdapter(value = "directory1Channel", poller = @Poller(fixedDelay = INTERVAL_IN_MILLIS))
    public MessageSource<File> directory1FileReadingMessageSource() {
        return super.createFileReadingMessageSource(directory1MetadataStore());
    }

    @Bean
    public ConcurrentMetadataStore directory1MetadataStore() {
        return super.createMetadataStore();
    }

    @Bean
    @ServiceActivator(inputChannel = "directory1Channel")
    public MessageHandler directory1MessageHandler() {
        return super.createMessageHandler(messagePublisher);
    }
}
