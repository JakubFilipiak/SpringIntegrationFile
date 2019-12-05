package pl.filipiak.jakub.training.fileintegration.utils;

import org.springframework.integration.file.DefaultDirectoryScanner;
import org.springframework.integration.file.filters.RegexPatternFileListFilter;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FilesByPatternsSearcher {

    private final File DIR;
    private final List<Pattern> PATTERNS;

    private Map<Pattern, List<Path>> results = new HashMap<>();

    public FilesByPatternsSearcher(File directory, List<Pattern> patterns) {
        this.DIR = directory;
        this.PATTERNS = patterns;
        search();
    }

    public Map<Pattern, List<Path>> getResults() {
        return results;
    }

    private void search() {
        PATTERNS.forEach(pattern -> {
            List<Path> foundFilesPaths = findFilesMatchingAPattern(pattern).stream()
                    .map(File::toPath)
                    .collect(Collectors.toList());
            results.put(pattern, foundFilesPaths);
        });
    }

    private List<File> findFilesMatchingAPattern(Pattern pattern) {
        DefaultDirectoryScanner scanner = new DefaultDirectoryScanner();
        scanner.setFilter(new RegexPatternFileListFilter(pattern));
        return scanner.listFiles(DIR);
    }
}
