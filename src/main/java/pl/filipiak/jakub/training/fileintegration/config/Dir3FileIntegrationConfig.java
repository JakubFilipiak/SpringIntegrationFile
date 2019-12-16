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
import pl.filipiak.jakub.training.fileintegration.config.filters.helpers.DefaultCompositeFileListFilterProvider;
import pl.filipiak.jakub.training.fileintegration.config.helpers.DefaultFileIntegrationConfigProvider;
import pl.filipiak.jakub.training.fileintegration.config.properties.Dir3ConfigProperties;
import pl.filipiak.jakub.training.fileintegration.integration.MessagePublisher;
import pl.filipiak.jakub.training.fileintegration.utils.FileSearcherResultsValidator;
import pl.filipiak.jakub.training.fileintegration.utils.MetadataStoreFactory;

import java.io.File;

@Configuration
@ConditionalOnProperty(
        prefix = "user-variables.integration.directories-scan-configs.config3",
        name = "enabled",
        havingValue = "true")
@EnableConfigurationProperties(Dir3ConfigProperties.class)
public class Dir3FileIntegrationConfig {

    private final String pollingIntervalInMillis = "1000";

    private final MetadataStoreFactory metadataStoreFactory;
    private final String metadataStoreFileName;

    private final DefaultCompositeFileListFilterProvider filterProvider;
    private final DefaultFileIntegrationConfigProvider configProvider;

    public Dir3FileIntegrationConfig(Dir3ConfigProperties properties,
                                     MetadataStoreFactory metadataStoreFactory,
                                     FileSearcherResultsValidator resultsValidator,
                                     MessagePublisher messagePublisher) {
        this.filterProvider = new DefaultCompositeFileListFilterProvider(
                properties.isDirectoriesValidationEnabled(),
                properties.getDirectoriesAcceptPattern(),
                properties.getMetadataKeyPrefix());
        this.configProvider = new DefaultFileIntegrationConfigProvider(
                properties,
                resultsValidator,
                messagePublisher);
        this.metadataStoreFactory = metadataStoreFactory;
        this.metadataStoreFileName = properties.getMetadataStoreFileName();
    }

    @Bean
    public MessageChannel directory3Channel() {
        return configProvider.createMessageChannel();
    }

    @Bean
    @InboundChannelAdapter(value = "directory3Channel", poller = @Poller(fixedDelay = pollingIntervalInMillis))
    public MessageSource<File> directory3FileReadingMessageSource() {
        return configProvider.createFileReadingMessageSource(
                filterProvider.createDefaultCompositeFileListFilter(directory3MetadataStore()));
    }

    @Bean
    public ConcurrentMetadataStore directory3MetadataStore() {
        return metadataStoreFactory.createPropertiesPersistingMetadataStore(metadataStoreFileName);
    }

    @Bean
    @ServiceActivator(inputChannel = "directory3Channel")
    public MessageHandler directory3MessageHandler() {
        return configProvider.createMessageHandler();
    }
}
