package pl.filipiak.jakub.training.fileintegration.utils;

import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Component
public class FileSearcherResultsValidator {

    private final int REQUIRED_NUMBER_OF_PATTERNS = 2;

    public boolean isCorrect(Map<Pattern, List<Path>> results) {
        return isNumberOfPatternsCorrect(results) &&
                isNotMoreThanOnePathForOnePattern(results) &&
                isOnePathForAtLeastOnePattern(results);
    }

    private boolean isNumberOfPatternsCorrect(Map<Pattern, List<Path>> results) {
        return results.keySet().size() == REQUIRED_NUMBER_OF_PATTERNS;
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
