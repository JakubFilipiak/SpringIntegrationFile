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
import pl.filipiak.jakub.training.fileintegration.config.properties.Dir4ConfigProperties;
import pl.filipiak.jakub.training.fileintegration.utils.FileSearcherResultsValidator;
import pl.filipiak.jakub.training.fileintegration.utils.MessagePublisher;

import java.io.File;

@Configuration
@ConditionalOnProperty(
        prefix = "user-variables.integration.directories-scan-configs.config4",
        name = "enabled",
        havingValue = "true")
@EnableConfigurationProperties(Dir4ConfigProperties.class)
public class Dir4FileIntegrationConfig {

    private final String POLLING_INTERVAL_IN_MILLIS = "1000";

    private DefaultFileIntegrationConfigProvider configProvider;

    public Dir4FileIntegrationConfig(Dir4ConfigProperties properties,
                                     FileSearcherResultsValidator resultsValidator,
                                     MessagePublisher messagePublisher) {
        this.configProvider = new DefaultFileIntegrationConfigProvider(
                properties,
                resultsValidator,
                messagePublisher);
    }

    @Bean
    public MessageChannel directory4Channel() {
        return configProvider.createMessageChannel();
    }

    @Bean
    @InboundChannelAdapter(value = "directory4Channel", poller = @Poller(fixedDelay = POLLING_INTERVAL_IN_MILLIS))
    public MessageSource<File> directory4FileReadingMessageSource() {
        return configProvider.createFileReadingMessageSource(directory4MetadataStore());
    }

    @Bean
    public ConcurrentMetadataStore directory4MetadataStore() {
        return configProvider.createMetadataStore();
    }

    @Bean
    @ServiceActivator(inputChannel = "directory4Channel")
    public MessageHandler directory4MessageHandler() {
        return configProvider.createMessageHandler();
    }
}
