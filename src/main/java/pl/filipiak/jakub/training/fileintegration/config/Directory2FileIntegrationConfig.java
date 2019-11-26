package pl.filipiak.jakub.training.fileintegration.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.file.RecursiveDirectoryScanner;
import org.springframework.integration.file.filters.CompositeFileListFilter;
import org.springframework.integration.metadata.ConcurrentMetadataStore;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import pl.filipiak.jakub.training.fileintegration.config.helpers.FileIntegrationAbstractConfig;
import pl.filipiak.jakub.training.fileintegration.config.properties.Directory2ConfigProperties;

import java.io.File;

@Configuration
@ConditionalOnProperty(
        prefix = "user-variables.integration.directories-scan-configs.config2",
        name = "enabled",
        havingValue = "true")
@EnableConfigurationProperties(Directory2ConfigProperties.class)
public class Directory2FileIntegrationConfig extends FileIntegrationAbstractConfig {

    private final String INTERVAL = INTERVAL_IN_MILLIS;
    private final String INPUT_DIR;
    private final String FILE_PATTERN;
    private final String KEY_PREFIX;

    public Directory2FileIntegrationConfig(Directory2ConfigProperties properties) {
        INPUT_DIR = properties.getStorageDirectory();
        FILE_PATTERN = properties.getFileAcceptPattern();
        KEY_PREFIX = properties.getMetadataKeyPrefix();
    }

    @Bean
    public MessageChannel directory2Channel() {
        return super.createMessageChannel();
    }

    @Bean
    @InboundChannelAdapter(value = "directory2Channel", poller = @Poller(fixedDelay = INTERVAL))
    public MessageSource<File> directory2FileReadingMessageSource() {
        return super.createFileReadingMessageSource(INPUT_DIR, directory2RecursiveScanner());
    }

    @Bean
    public RecursiveDirectoryScanner directory2RecursiveScanner() {
        return super.createRecursiveDirectoryScanner(directory2CompositeFileListFilter());
    }

    @Bean
    public CompositeFileListFilter<File> directory2CompositeFileListFilter() {
        return super.createCompositeFileListFilter(FILE_PATTERN, directory2MetadataStore(), KEY_PREFIX);
    }

    @Bean
    public ConcurrentMetadataStore directory2MetadataStore() {
        return super.createMetadataStore("directory2");
    }

    @Bean
    @ServiceActivator(inputChannel= "directory2Channel")
    public MessageHandler directory2MessageHandler() {
        return super.createMessageHandler();
    }
}
