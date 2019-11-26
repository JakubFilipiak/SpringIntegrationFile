package pl.filipiak.jakub.training.fileintegration.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.RecursiveDirectoryScanner;
import org.springframework.integration.file.filters.AcceptOnceFileListFilter;
import org.springframework.integration.file.filters.CompositeFileListFilter;
import org.springframework.integration.file.filters.FileSystemPersistentAcceptOnceFileListFilter;
import org.springframework.integration.file.filters.SimplePatternFileListFilter;
import org.springframework.integration.metadata.ConcurrentMetadataStore;
import org.springframework.integration.metadata.PropertiesPersistingMetadataStore;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import pl.filipiak.jakub.training.fileintegration.config.properties.FileIntegrationProperties;

import java.io.File;

@Slf4j
@Configuration
@EnableConfigurationProperties(FileIntegrationProperties.class)
public class FileIntegrationConfig {

    private final String INTERVAL_IN_MILLIS = "10000";
    private final String INPUT_DIR;
    private final String FILE_PATTERN;
    private final String KEY_PREFIX;

    public FileIntegrationConfig(FileIntegrationProperties fileIntegrationProperties) {
        INPUT_DIR = fileIntegrationProperties.getStorageDirectory();
        FILE_PATTERN = fileIntegrationProperties.getAcceptPattern();
        KEY_PREFIX = fileIntegrationProperties.getMetadataKeyPrefix();
    }

    @Bean
    public MessageChannel fileChannel() {
        return new DirectChannel();
    }

    @Bean
    @InboundChannelAdapter(value = "fileChannel", poller = @Poller(fixedDelay = INTERVAL_IN_MILLIS))
    public MessageSource<File> fileReadingMessageSource() {
        FileReadingMessageSource source = new FileReadingMessageSource();
        source.setDirectory(new File(INPUT_DIR));
        source.setScanner(recursiveDirectoryScanner());
        return source;
    }

    @Bean
    @ServiceActivator(inputChannel= "fileChannel")
    public MessageHandler messageHandler() {
        return message -> {
            File file = (File) message.getPayload();
            log.info("New file: " + file.getAbsolutePath());
        };
    }

    @Bean
    public RecursiveDirectoryScanner recursiveDirectoryScanner() {
        RecursiveDirectoryScanner scanner = new RecursiveDirectoryScanner();
        scanner.setFilter(compositeFileListFilter());
        return scanner;
    }

    @Bean
    public CompositeFileListFilter<File> compositeFileListFilter() {
        CompositeFileListFilter<File> filters = new CompositeFileListFilter<>();
        filters.addFilter(new SimplePatternFileListFilter(FILE_PATTERN));
        filters.addFilter(new AcceptOnceFileListFilter<>());
        filters.addFilter(fileSystemPersistentAcceptOnceFileListFilter());
        return filters;
    }

    @Bean
    public FileSystemPersistentAcceptOnceFileListFilter fileSystemPersistentAcceptOnceFileListFilter() {
        FileSystemPersistentAcceptOnceFileListFilter filter =
                new FileSystemPersistentAcceptOnceFileListFilter(metadataStore(), KEY_PREFIX);
        filter.setFlushOnUpdate(true);
        return filter;
    }

    @Bean
    public ConcurrentMetadataStore metadataStore() {
        return new PropertiesPersistingMetadataStore();
    }
}
