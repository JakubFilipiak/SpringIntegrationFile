package pl.filipiak.jakub.training.fileintegration.config.helpers;

import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.RecursiveDirectoryScanner;
import org.springframework.integration.file.filters.AbstractDirectoryAwareFileListFilter;
import org.springframework.integration.file.filters.AbstractPersistentAcceptOnceFileListFilter;
import org.springframework.integration.file.filters.CompositeFileListFilter;
import org.springframework.integration.metadata.ConcurrentMetadataStore;
import org.springframework.integration.metadata.PropertiesPersistingMetadataStore;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import pl.filipiak.jakub.training.fileintegration.config.filters.AcceptDirectoryByRegexFilter;
import pl.filipiak.jakub.training.fileintegration.config.filters.AcceptEveryDirectoryFilter;
import pl.filipiak.jakub.training.fileintegration.config.filters.AcceptFileOnlyOnceFilter;
import pl.filipiak.jakub.training.fileintegration.utils.FileSearchingMessageHandler;
import pl.filipiak.jakub.training.fileintegration.utils.MessagePublisher;

import java.io.File;

public abstract class AbstractFileIntegrationConfig {

    private String configId;
    private String configName;

    protected static final String INTERVAL_IN_MILLIS = "1000";
    private String inputDir;
    private String file1Pattern;
    private String file2Pattern;
    private String metadataKeyPrefix;
    private boolean dirValidationEnabled;
    private String dirPattern;

    public AbstractFileIntegrationConfig(String configId,
                                         String configName,
                                         String inputDir,
                                         String file1Pattern,
                                         String file2Pattern,
                                         String metadataKeyPrefix,
                                         boolean dirValidationEnabled,
                                         String dirPattern) {
        this.configId = configId;
        this.configName = configName;
        this.inputDir = inputDir;
        this.file1Pattern = file1Pattern;
        this.file2Pattern = file2Pattern;
        this.metadataKeyPrefix = metadataKeyPrefix;
        this.dirValidationEnabled = dirValidationEnabled;
        this.dirPattern = dirPattern;
    }

    protected MessageChannel createMessageChannel() {
        return new DirectChannel();
    }

    protected MessageSource<File> createFileReadingMessageSource(
            ConcurrentMetadataStore metadataStore) {
        FileReadingMessageSource source = new FileReadingMessageSource();
        source.setDirectory(new File(inputDir));
        source.setScanner(createRecursiveDirectoryScanner(metadataStore));
        return source;
    }

    protected RecursiveDirectoryScanner createRecursiveDirectoryScanner(
            ConcurrentMetadataStore metadataStore) {
        RecursiveDirectoryScanner scanner = new RecursiveDirectoryScanner();
        scanner.setFilter(createCompositeFileListFilter(metadataStore));
        return scanner;
    }

    protected CompositeFileListFilter<File> createCompositeFileListFilter(
            ConcurrentMetadataStore metadataStore) {
        CompositeFileListFilter<File> filters = new CompositeFileListFilter<>();
        filters.addFilter(createAcceptDirectoriesFilter());
        filters.addFilter(createAcceptFilesOnlyOnceFilter(metadataStore));
        return filters;
    }

    protected AbstractDirectoryAwareFileListFilter<File> createAcceptDirectoriesFilter() {
        if (dirValidationEnabled && dirPattern != null && !dirPattern.isEmpty())
            return new AcceptDirectoryByRegexFilter(dirPattern);
        return new AcceptEveryDirectoryFilter();
    }

    protected AbstractPersistentAcceptOnceFileListFilter<File> createAcceptFilesOnlyOnceFilter(
            ConcurrentMetadataStore metadataStore) {
        return new AcceptFileOnlyOnceFilter(metadataStore, metadataKeyPrefix);
    }

    protected ConcurrentMetadataStore createMetadataStore() {
        PropertiesPersistingMetadataStore metadataStore = new PropertiesPersistingMetadataStore();
        metadataStore.setFileName(configName + "-metadata-store.properties");
        return metadataStore;
    }

    protected MessageHandler createMessageHandler(MessagePublisher messagePublisher) {
        return new FileSearchingMessageHandler(configId, file1Pattern, file2Pattern, messagePublisher);
    }
}
