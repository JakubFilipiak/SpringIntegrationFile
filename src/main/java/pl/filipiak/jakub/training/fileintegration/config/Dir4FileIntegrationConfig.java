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
import pl.filipiak.jakub.training.fileintegration.config.properties.Dir1ConfigProperties;
import pl.filipiak.jakub.training.fileintegration.config.properties.Dir4ConfigProperties;
import pl.filipiak.jakub.training.fileintegration.utils.FileSearcherResultsValidator;
import pl.filipiak.jakub.training.fileintegration.integration.MessagePublisher;
import pl.filipiak.jakub.training.fileintegration.utils.MetadataStoreFactory;

import java.io.File;

@Configuration
@ConditionalOnProperty(
        prefix = "user-variables.integration.directories-scan-configs.config4",
        name = "enabled",
        havingValue = "true")
@EnableConfigurationProperties(Dir4ConfigProperties.class)
public class Dir4FileIntegrationConfig {

    private final String pollingIntervalInMillis = "1000";

    private final MetadataStoreFactory metadataStoreFactory;
    private final String metadataStoreFileName;

    private final DefaultCompositeFileListFilterProvider filterProvider;
    private final DefaultFileIntegrationConfigProvider configProvider;

    public Dir4FileIntegrationConfig(Dir4ConfigProperties properties,
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
    public MessageChannel directory4Channel() {
        return configProvider.createMessageChannel();
    }

    @Bean
    @InboundChannelAdapter(value = "directory4Channel", poller = @Poller(fixedDelay = pollingIntervalInMillis))
    public MessageSource<File> directory4FileReadingMessageSource() {
        return configProvider.createFileReadingMessageSource(
                filterProvider.createDefaultCompositeFileListFilter(directory4MetadataStore()));
    }

    @Bean
    public ConcurrentMetadataStore directory4MetadataStore() {
        return metadataStoreFactory.createPropertiesPersistingMetadataStore(metadataStoreFileName);
    }

    @Bean
    @ServiceActivator(inputChannel = "directory4Channel")
    public MessageHandler directory4MessageHandler() {
        return configProvider.createMessageHandler();
    }
}
