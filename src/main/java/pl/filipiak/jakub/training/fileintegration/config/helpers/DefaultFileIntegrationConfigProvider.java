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
import pl.filipiak.jakub.training.fileintegration.config.properties.helpers.AbstractDirConfigProperties;
import pl.filipiak.jakub.training.fileintegration.utils.FileSearchingMessageHandler;
import pl.filipiak.jakub.training.fileintegration.utils.MessagePublisher;

import java.io.File;

public class DefaultFileIntegrationConfigProvider {

    private String configId;
    private String inputDir;
    private String file1Pattern;
    private String file2Pattern;
    private String metadataStoreFileName;
    private String metadataKeyPrefix;
    private boolean dirValidationEnabled;
    private String dirPattern;

    private MessagePublisher messagePublisher;

    public DefaultFileIntegrationConfigProvider(AbstractDirConfigProperties properties,
                                                MessagePublisher messagePublisher) {
        this.configId = properties.getId();
        this.inputDir = properties.getStorageDirectory();
        this.file1Pattern = properties.getFile1AcceptPattern();
        this.file2Pattern = properties.getFile2AcceptPattern();
        this.metadataStoreFileName = properties.getMetadataStoreFileName();
        this.metadataKeyPrefix = properties.getMetadataKeyPrefix();
        this.dirValidationEnabled = properties.isDirectoriesValidationEnabled();
        this.dirPattern = properties.getDirectoriesAcceptPattern();

        this.messagePublisher = messagePublisher;
    }

    public MessageChannel createMessageChannel() {
        return new DirectChannel();
    }

    public MessageSource<File> createFileReadingMessageSource(
            ConcurrentMetadataStore metadataStore) {
        FileReadingMessageSource source = new FileReadingMessageSource();
        source.setDirectory(new File(inputDir));
        source.setScanner(createRecursiveDirectoryScanner(metadataStore));
        return source;
    }

    public ConcurrentMetadataStore createMetadataStore() {
        PropertiesPersistingMetadataStore metadataStore = new PropertiesPersistingMetadataStore();
        metadataStore.setFileName(metadataStoreFileName + ".properties");
        return metadataStore;
    }

    public MessageHandler createMessageHandler() {
        return new FileSearchingMessageHandler(configId, file1Pattern, file2Pattern, messagePublisher);
    }

    private RecursiveDirectoryScanner createRecursiveDirectoryScanner(
            ConcurrentMetadataStore metadataStore) {
        RecursiveDirectoryScanner scanner = new RecursiveDirectoryScanner();
        scanner.setFilter(createCompositeFileListFilter(metadataStore));
        return scanner;
    }

    private CompositeFileListFilter<File> createCompositeFileListFilter(
            ConcurrentMetadataStore metadataStore) {
        CompositeFileListFilter<File> filters = new CompositeFileListFilter<>();
        filters.addFilter(createAcceptDirectoriesFilter());
        filters.addFilter(createAcceptFilesOnlyOnceFilter(metadataStore));
        return filters;
    }

    private AbstractDirectoryAwareFileListFilter<File> createAcceptDirectoriesFilter() {
        if (dirValidationEnabled && dirPattern != null && !dirPattern.isEmpty())
            return new AcceptDirectoryByRegexFilter(dirPattern);
        return new AcceptEveryDirectoryFilter();
    }

    private AbstractPersistentAcceptOnceFileListFilter<File> createAcceptFilesOnlyOnceFilter(
            ConcurrentMetadataStore metadataStore) {
        return new AcceptFileOnlyOnceFilter(metadataStore, metadataKeyPrefix);
    }
}
