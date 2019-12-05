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
import pl.filipiak.jakub.training.fileintegration.config.properties.Dir1ConfigProperties;
import pl.filipiak.jakub.training.fileintegration.utils.FileSearcherResultsValidator;
import pl.filipiak.jakub.training.fileintegration.utils.MessagePublisher;

import java.io.File;

@Configuration
@ConditionalOnProperty(
        prefix = "user-variables.integration.directories-scan-configs.config1",
        name = "enabled",
        havingValue = "true")
@EnableConfigurationProperties(Dir1ConfigProperties.class)
public class Dir1FileIntegrationConfig {

    private final String POLLING_INTERVAL_IN_MILLIS = "1000";

    private DefaultFileIntegrationConfigProvider configProvider;

    public Dir1FileIntegrationConfig(Dir1ConfigProperties properties,
                                     FileSearcherResultsValidator resultsValidator,
                                     MessagePublisher messagePublisher) {
        this.configProvider = new DefaultFileIntegrationConfigProvider(
                properties,
                resultsValidator,
                messagePublisher);
    }

    @Bean
    public MessageChannel directory1Channel() {
        return configProvider.createMessageChannel();
    }

    @Bean
    @InboundChannelAdapter(value = "directory1Channel", poller = @Poller(fixedDelay = POLLING_INTERVAL_IN_MILLIS))
    public MessageSource<File> directory1FileReadingMessageSource() {
        return configProvider.createFileReadingMessageSource(directory1MetadataStore());
    }

    @Bean
    public ConcurrentMetadataStore directory1MetadataStore() {
        return configProvider.createMetadataStore();
    }

    @Bean
    @ServiceActivator(inputChannel = "directory1Channel")
    public MessageHandler directory1MessageHandler() {
        return configProvider.createMessageHandler();
    }
}
