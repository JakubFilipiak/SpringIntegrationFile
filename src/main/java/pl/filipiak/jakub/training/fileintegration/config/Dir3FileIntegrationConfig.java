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
import pl.filipiak.jakub.training.fileintegration.config.helpers.DefaultFileIntegrationConfigProvider;
import pl.filipiak.jakub.training.fileintegration.config.properties.Dir3ConfigProperties;
import pl.filipiak.jakub.training.fileintegration.utils.MessagePublisher;

import java.io.File;

@Configuration
@ConditionalOnProperty(
        prefix = "user-variables.integration.directories-scan-configs.config3",
        name = "enabled",
        havingValue = "true")
@EnableConfigurationProperties(Dir3ConfigProperties.class)
public class Dir3FileIntegrationConfig {

    private final String POLLING_INTERVAL_IN_MILLIS = "1000";

    private DefaultFileIntegrationConfigProvider configProvider;

    public Dir3FileIntegrationConfig(Dir3ConfigProperties properties, MessagePublisher messagePublisher) {
        this.configProvider = new DefaultFileIntegrationConfigProvider(properties, messagePublisher);
    }

    @Bean
    public MessageChannel directory3Channel() {
        return configProvider.createMessageChannel();
    }

    @Bean
    @InboundChannelAdapter(value = "directory3Channel", poller = @Poller(fixedDelay = POLLING_INTERVAL_IN_MILLIS))
    public MessageSource<File> directory3FileReadingMessageSource() {
        return configProvider.createFileReadingMessageSource(directory3MetadataStore());
    }

    @Bean
    public ConcurrentMetadataStore directory3MetadataStore() {
        return configProvider.createMetadataStore();
    }

    @Bean
    @ServiceActivator(inputChannel = "directory3Channel")
    public MessageHandler directory3MessageHandler() {
        return configProvider.createMessageHandler();
    }
}
