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
import pl.filipiak.jakub.training.fileintegration.config.properties.Directory1ConfigProperties;

import java.io.File;

@Configuration
@ConditionalOnProperty(
        prefix = "user-variables.integration.directories-scan-configs.config1",
        name = "enabled",
        havingValue = "true")
@EnableConfigurationProperties(Directory1ConfigProperties.class)
public class Directory1FileIntegrationConfig extends FileIntegrationAbstractConfig {

    private final String INTERVAL = INTERVAL_IN_MILLIS;
    private final String INPUT_DIR;
    private final String FILE_PATTERN;
    private final String KEY_PREFIX;

    public Directory1FileIntegrationConfig(Directory1ConfigProperties properties) {
        INPUT_DIR = properties.getStorageDirectory();
        FILE_PATTERN = properties.getFileAcceptPattern();
        KEY_PREFIX = properties.getMetadataKeyPrefix();
    }

    @Bean
    public MessageChannel directory1Channel() {
        return super.createMessageChannel();
    }

    @Bean
    @InboundChannelAdapter(value = "directory1Channel", poller = @Poller(fixedDelay = INTERVAL))
    public MessageSource<File> directory1FileReadingMessageSource() {
        return super.createFileReadingMessageSource(INPUT_DIR, directory1RecursiveScanner());
    }

    @Bean
    public RecursiveDirectoryScanner directory1RecursiveScanner() {
        return super.createRecursiveDirectoryScanner(directory1CompositeFileListFilter());
    }

    @Bean
    public CompositeFileListFilter<File> directory1CompositeFileListFilter() {
        return super.createCompositeFileListFilter(FILE_PATTERN, directory1MetadataStore(), KEY_PREFIX);
    }

    @Bean
    public ConcurrentMetadataStore directory1MetadataStore() {
        return super.createMetadataStore("directory1");
    }

    @Bean
    @ServiceActivator(inputChannel= "directory1Channel")
    public MessageHandler directory1MessageHandler() {
        return super.createMessageHandler();
    }
}
