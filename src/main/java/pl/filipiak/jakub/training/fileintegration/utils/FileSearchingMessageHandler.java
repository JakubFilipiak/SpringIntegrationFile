package pl.filipiak.jakub.training.fileintegration.utils;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import pl.filipiak.jakub.training.fileintegration.integration.MessagePublisher;
import pl.filipiak.jakub.training.fileintegration.integration.PathsContainingMessagePayload;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FileSearchingMessageHandler implements MessageHandler {

    private final String configId;
    private final List<Pattern> patterns;

    private final FileSearcherResultsValidator resultsValidator;
    private final MessagePublisher messagePublisher;

    public FileSearchingMessageHandler(String configId,
                                       String pattern1,
                                       String pattern2,
                                       FileSearcherResultsValidator resultsValidator,
                                       MessagePublisher messagePublisher) {
        this.configId = configId;
        this.patterns = initPatterns(Arrays.asList(pattern1, pattern2));
        this.resultsValidator = resultsValidator;
        this.messagePublisher = messagePublisher;
    }

    @Override
    public void handleMessage(Message<?> message) {
        Object inputPayload = message.getPayload();
        if (!(inputPayload instanceof File))
            return;
        File directory = (File) inputPayload;
        FilesByPatternsSearcher filesSearcher = new FilesByPatternsSearcher(directory, patterns);
        Map<Pattern, List<Path>> results = filesSearcher.getResults();
        if (resultsValidator.isCorrect(results))
            messagePublisher.publishWithPayload(assembleOutputPayload(results));
    }

    private List<Pattern> initPatterns(List<String> rawPatterns) {
        return rawPatterns.stream()
                .filter(Objects::nonNull)
                .map(Pattern::compile)
                .collect(Collectors.toList());
    }

    private PathsContainingMessagePayload assembleOutputPayload(Map<Pattern, List<Path>> results) {
        PathsContainingMessagePayload outputPayload = new PathsContainingMessagePayload(configId);
        assignPathsToOutputPayload(results, outputPayload);
        return outputPayload;
    }

    private void assignPathsToOutputPayload(Map<Pattern, List<Path>> results,
                                            PathsContainingMessagePayload outputPayload) {
        results.keySet().forEach(pattern -> {
            Path path = retrievePath(results.get(pattern));
            if (isEqualToFirstPattern(pattern))
                outputPayload.setFile1Path(path);
            else if (isEqualToSecondPattern(pattern))
                outputPayload.setFile2Path(path);
        });
    }

    private Path retrievePath(List<Path> paths) {
        if (paths.size() == 0) return null;
        return paths.get(0);
    }

    private boolean isEqualToFirstPattern(Pattern pattern) {
        return pattern.equals(patterns.get(0));
    }

    private boolean isEqualToSecondPattern(Pattern pattern) {
        return pattern.equals(patterns.get(1));
    }
}
