package pl.filipiak.jakub.training.fileintegration.config.helpers;

import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.RecursiveDirectoryScanner;
import org.springframework.integration.file.filters.CompositeFileListFilter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import pl.filipiak.jakub.training.fileintegration.config.properties.helpers.AbstractDirConfigProperties;
import pl.filipiak.jakub.training.fileintegration.integration.MessagePublisher;
import pl.filipiak.jakub.training.fileintegration.utils.FileSearcherResultsValidator;
import pl.filipiak.jakub.training.fileintegration.utils.FileSearchingMessageHandler;

import java.io.File;

public class DefaultFileIntegrationConfigProvider {

    private final String configId;
    private final String inputDir;
    private final String file1Pattern;
    private final String file2Pattern;

    private final FileSearcherResultsValidator resultsValidator;
    private final MessagePublisher messagePublisher;

    public DefaultFileIntegrationConfigProvider(AbstractDirConfigProperties properties,
                                                FileSearcherResultsValidator resultsValidator,
                                                MessagePublisher messagePublisher) {
        this.configId = properties.getId();
        this.inputDir = properties.getStorageDirectory();
        this.file1Pattern = properties.getFile1AcceptPattern();
        this.file2Pattern = properties.getFile2AcceptPattern();

        this.resultsValidator = resultsValidator;
        this.messagePublisher = messagePublisher;
    }

    public MessageChannel createMessageChannel() {
        return new DirectChannel();
    }

    public MessageSource<File> createFileReadingMessageSource(CompositeFileListFilter<File> filter) {
        FileReadingMessageSource source = new FileReadingMessageSource();
        source.setDirectory(new File(inputDir));
        source.setScanner(createRecursiveDirectoryScanner(filter));
        return source;
    }

    public MessageHandler createMessageHandler() {
        return new FileSearchingMessageHandler(
                configId,
                file1Pattern,
                file2Pattern,
                resultsValidator,
                messagePublisher);
    }

    private RecursiveDirectoryScanner createRecursiveDirectoryScanner(CompositeFileListFilter<File> filter) {
        RecursiveDirectoryScanner scanner = new RecursiveDirectoryScanner();
        scanner.setFilter(filter);
        return scanner;
    }
}
