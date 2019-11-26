package pl.filipiak.jakub.training.fileintegration.config.helpers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.file.DirectoryScanner;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.RecursiveDirectoryScanner;
import org.springframework.integration.file.filters.*;
import org.springframework.integration.metadata.ConcurrentMetadataStore;
import org.springframework.integration.metadata.PropertiesPersistingMetadataStore;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import java.io.File;

@Slf4j
public abstract class FileIntegrationAbstractConfig {

    protected static final String INTERVAL_IN_MILLIS = "1000";

    protected MessageChannel createMessageChannel() {
        return new DirectChannel();
    }

    protected MessageSource<File> createFileReadingMessageSource(String directory, DirectoryScanner scanner) {
        FileReadingMessageSource source = new FileReadingMessageSource();
        source.setDirectory(new File(directory));
        source.setScanner(scanner);
        return source;
    }

    protected RecursiveDirectoryScanner createRecursiveDirectoryScanner(CompositeFileListFilter<File> filter) {
        RecursiveDirectoryScanner scanner = new RecursiveDirectoryScanner();
        scanner.setFilter(filter);
        return scanner;
    }

    protected CompositeFileListFilter<File> createCompositeFileListFilter(
            String filePattern, ConcurrentMetadataStore metadataStore, String keyPrefix) {
        CompositeFileListFilter<File> filters = new CompositeFileListFilter<>();
        filters.addFilter(new RegexPatternFileListFilter(filePattern));
        filters.addFilter(new AcceptOnceFileListFilter<>());
        filters.addFilter(createFileSystemPersistentAcceptOnceFileListFilter(metadataStore, keyPrefix));
        return filters;
    }

    protected ConcurrentMetadataStore createMetadataStore(String filename) {
        PropertiesPersistingMetadataStore metadataStore = new PropertiesPersistingMetadataStore();
        metadataStore.setFileName(filename + ".properties");
        return metadataStore;
    }

    private FileSystemPersistentAcceptOnceFileListFilter createFileSystemPersistentAcceptOnceFileListFilter(
            ConcurrentMetadataStore metadataStore, String keyPrefix) {
        FileSystemPersistentAcceptOnceFileListFilter filter =
                new FileSystemPersistentAcceptOnceFileListFilter(metadataStore, keyPrefix);
        filter.setFlushOnUpdate(true);
        return filter;
    }

    protected MessageHandler createMessageHandler() {
        return message -> {
            File file = (File) message.getPayload();
            log.info("New file: " + file.getAbsolutePath());
        };
    }
}
