package pl.filipiak.jakub.training.fileintegration.utils

import spock.lang.Specification

import java.nio.file.Path
import java.nio.file.Paths
import java.util.regex.Pattern

class FileSearcherResultsValidatorTest extends Specification {

    private Pattern pattern1
    private Pattern pattern2
    private Pattern pattern3
    private Path path1
    private Path path2
    private Path path3
    private Map<Pattern, List<Path>> results
    private FileSearcherResultsValidator validator

    def setup() {
        pattern1 = Pattern.compile(".*")
        pattern2 = Pattern.compile(".+")
        pattern3 = Pattern.compile(".*+/")
        path1 = Paths.get("").toAbsolutePath()
        path2 = Paths.get("").toAbsolutePath()
        path3 = Paths.get("").toAbsolutePath()
        validator = new FileSearcherResultsValidator()
        results = new HashMap<>()
    }

    def "isCorrect should return false when too many patterns"() {
        given:
        results.put(pattern1, new ArrayList<Path>())
        results.put(pattern2, new ArrayList<Path>())
        results.put(pattern3, new ArrayList<Path>())

        when:
        boolean isCorrect = validator.isCorrect(results)

        then:
        !isCorrect
    }

    def "isCorrect should return false when too little patterns"() {
        given:
        results.put(pattern1, new ArrayList<Path>())

        when:
        boolean isCorrect = validator.isCorrect(results)

        then:
        !isCorrect
    }

    def "isCorrect should return false when more than one path for one pattern"() {
        given:
        results.put(pattern1, Arrays.asList(path1, path2))
        results.put(pattern2, Arrays.asList(path3))

        when:
        boolean isCorrect = validator.isCorrect(results)

        then:
        !isCorrect
    }

    def "isCorrect should return false when no paths for all patterns"() {
        given:
        results.put(pattern1, new ArrayList<Path>())
        results.put(pattern2, new ArrayList<Path>())

        when:
        boolean isCorrect = validator.isCorrect(results)

        then:
        !isCorrect
    }

    def "isCorrect should return true when two patterns, one path for first pattern, no path for second"() {
        given:
        results.put(pattern1, Arrays.asList(path1))
        results.put(pattern2, new ArrayList<Path>())

        when:
        boolean isCorrect = validator.isCorrect(results)

        then:
        isCorrect
    }

    def "isCorrect should return true when two patterns, one path for first pattern, one path for second"() {
        given:
        results.put(pattern1, Arrays.asList(path1))
        results.put(pattern2, Arrays.asList(path2))

        when:
        boolean isCorrect = validator.isCorrect(results)

        then:
        isCorrect
    }
}
