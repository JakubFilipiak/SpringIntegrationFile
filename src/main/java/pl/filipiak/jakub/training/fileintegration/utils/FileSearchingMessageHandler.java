package pl.filipiak.jakub.training.fileintegration.utils;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FileSearchingMessageHandler implements MessageHandler {

    private final String CONFIG_ID;
    private final List<Pattern> PATTERNS;

    private FileSearcherResultsValidator resultsValidator;
    private MessagePublisher messagePublisher;

    public FileSearchingMessageHandler(String configId,
                                       String pattern1,
                                       String pattern2,
                                       FileSearcherResultsValidator resultsValidator,
                                       MessagePublisher messagePublisher) {
        this.CONFIG_ID = configId;
        this.PATTERNS = initPatterns(Arrays.asList(pattern1, pattern2));
        this.resultsValidator = resultsValidator;
        this.messagePublisher = messagePublisher;
    }

    @Override
    public void handleMessage(Message<?> message) {
        Object inputPayload = message.getPayload();
        if (!(inputPayload instanceof File)) return;

        File directory = (File) inputPayload;
        FilesByPatternsSearcher filesSearcher = new FilesByPatternsSearcher(directory, PATTERNS);
        Map<Pattern, List<Path>> results = filesSearcher.getResults();
        if (resultsValidator.isCorrect(results)) {
            PathsContainingMessagePayload outputPayload = assembleOutputPayload(results);
            messagePublisher.publishWithPayload(outputPayload);
        }
    }

    private List<Pattern> initPatterns(List<String> rawPatterns) {
        return rawPatterns.stream()
                .map(Pattern::compile)
                .collect(Collectors.toList());
    }

    private PathsContainingMessagePayload assembleOutputPayload(Map<Pattern, List<Path>> results) {
        PathsContainingMessagePayload outputPayload = new PathsContainingMessagePayload(CONFIG_ID);
        assignPathsToOutputPayload(results, outputPayload);
        return outputPayload;
    }

    private void assignPathsToOutputPayload(Map<Pattern, List<Path>> results,
                                            PathsContainingMessagePayload outputPayload) {
        results.keySet().forEach(pattern -> {
            Path path = retrievePath(results.get(pattern));
            if (pattern.equals(PATTERNS.get(0))) {
                outputPayload.setFile1Path(path);
            } else if (pattern.equals(PATTERNS.get(1))) {
                outputPayload.setFile2Path(path);
            }
        });
    }

    private Path retrievePath(List<Path> paths) {
        if (paths.size() == 0) return null;
        return paths.get(0);
    }
}
