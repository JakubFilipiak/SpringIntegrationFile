package pl.filipiak.jakub.training.fileintegration.utils;

import org.springframework.integration.file.DefaultDirectoryScanner;
import org.springframework.integration.file.filters.RegexPatternFileListFilter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public class PairOfFilesSearcher {

    private final File DIR;
    private final String FILE1_PATTERN;
    private final String FILE2_PATTERN;

    private File file1Result;
    private File file2Result;

    public PairOfFilesSearcher(File directory, String file1Pattern, String file2Pattern) {
        this.DIR = directory;
        this.FILE1_PATTERN = file1Pattern;
        this.FILE2_PATTERN = file2Pattern;
    }

    public void search() throws FileNotFoundException, MoreFilesThanExpectedException {
        file1Result = findFileMatchingAPattern(DIR, FILE1_PATTERN);
        file2Result = findFileMatchingAPattern(DIR, FILE2_PATTERN);
        if (!isAtLeastOneFilePresent()) throw new FileNotFoundException(
                "No files matching patterns " +
                "'" + FILE1_PATTERN + "' and '" + FILE2_PATTERN + "' " +
                "found in directory '" + DIR + "'");
    }

    public String getResultsStringRepresentation() {
        String file1Path = null;
        String file2Path = null;
        if (file1Result != null) file1Path = file1Result.getAbsolutePath();
        if (file2Result != null) file2Path = file2Result.getAbsolutePath();
        return file1Path + ", " + file2Path;
    }

    private File findFileMatchingAPattern(File dir, String pattern) throws MoreFilesThanExpectedException {
        DefaultDirectoryScanner scanner = new DefaultDirectoryScanner();
        scanner.setFilter(new RegexPatternFileListFilter(pattern));
        List<File> results = scanner.listFiles(dir);
        if (results == null || results.size() == 0)
            return null;
        if (results.size() > 1)
            throw new MoreFilesThanExpectedException(
                    "More than one file matching a pattern " +
                    "'" + pattern + "' " +
                    "found in directory '" + dir + "'");
        return results.get(0);
    }

    private boolean isAtLeastOneFilePresent() {
        return file1Result != null || file2Result != null;
    }
}
