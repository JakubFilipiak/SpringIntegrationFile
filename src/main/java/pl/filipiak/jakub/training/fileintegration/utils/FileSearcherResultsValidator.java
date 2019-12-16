package pl.filipiak.jakub.training.fileintegration.utils;

import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Component
public class FileSearcherResultsValidator {

    public boolean isCorrect(Map<Pattern, List<Path>> results) {
        return isNumberOfPatternsCorrect(results) &&
                isNotMoreThanOnePathForOnePattern(results) &&
                isOnePathForAtLeastOnePattern(results);
    }

    private boolean isNumberOfPatternsCorrect(Map<Pattern, List<Path>> results) {
        final int requiredNumberOfPatterns = 2;
        return results.keySet().size() == requiredNumberOfPatterns;
    }

    private boolean isNotMoreThanOnePathForOnePattern(Map<Pattern, List<Path>> results) {
        for (Pattern pattern : results.keySet())
            if (results.get(pattern).size() > 1) return false;
        return true;
    }

    private boolean isOnePathForAtLeastOnePattern(Map<Pattern, List<Path>> results) {
        for (Pattern pattern : results.keySet())
            if (results.get(pattern).size() == 1) return true;
        return false;
    }
}
