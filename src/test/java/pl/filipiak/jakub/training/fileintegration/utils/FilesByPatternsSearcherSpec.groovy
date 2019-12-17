package pl.filipiak.jakub.training.fileintegration.utils

import org.springframework.util.FileSystemUtils
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.regex.Pattern

class FilesByPatternsSearcherSpec extends Specification {

    private static final Pattern CORRECT_FILE1_PATTERN = Pattern.compile(/(.+)+(\.(txt))/)
    private static final Pattern CORRECT_FILE2_PATTERN = Pattern.compile(/(.+)+(\.(csv))/)
    private static final String EXTENSION_OF_FILE1_PATTERN = ".txt"
    private static final String EXTENSION_OF_FILE2_PATTERN = ".csv"
    private static Path dirPath

    def setupSpec() {
        String dirPathInsideProjectDir = "target/test-resources"
        dirPath = Paths.get(new File(dirPathInsideProjectDir).getAbsolutePath())
        deleteDir()
    }

    def setup() {
        createDir()
    }

    def cleanup() {
        deleteDir()
    }

    def cleanupSpec() {
        deleteDir()
    }

    def createDir() {
        if (!dirPath.toFile().exists())
            Files.createDirectory(dirPath)
    }

    def deleteDir() {
        FileSystemUtils.deleteRecursively(dirPath)
    }

    def "Should correct find many files"() {
        given:
        File directory = dirPath.toFile()
        List<Pattern> patterns = Arrays.asList(CORRECT_FILE1_PATTERN, CORRECT_FILE2_PATTERN)

        Path path1ForFile1Pattern = createNewFile(EXTENSION_OF_FILE1_PATTERN)
        Path path2ForFile1Pattern = createNewFile(EXTENSION_OF_FILE1_PATTERN)
        Path path3ForFile1Pattern = createNewFile(EXTENSION_OF_FILE1_PATTERN)
        Path path1ForFile2Pattern = createNewFile(EXTENSION_OF_FILE2_PATTERN)
        Path path2ForFile2Pattern = createNewFile(EXTENSION_OF_FILE2_PATTERN)
        Path path3ForFile2Pattern = createNewFile(EXTENSION_OF_FILE2_PATTERN)

        Map<Pattern, List<Path>> expectedResults = new HashMap<>()
        expectedResults.put(CORRECT_FILE1_PATTERN, Arrays.asList(path1ForFile1Pattern,
                path2ForFile1Pattern, path3ForFile1Pattern))
        expectedResults.put(CORRECT_FILE2_PATTERN, Arrays.asList(path1ForFile2Pattern,
                path2ForFile2Pattern, path3ForFile2Pattern))

        when:
        FilesByPatternsSearcher searcher = new FilesByPatternsSearcher(directory, patterns)
        Map<Pattern, List<Path>> results = searcher.getResults()

        then:
        isEachMapTheSame(results, expectedResults)
    }

    def "Should correct find a pair of files"() {
        given:
        File directory = dirPath.toFile()
        List<Pattern> patterns = Arrays.asList(CORRECT_FILE1_PATTERN, CORRECT_FILE2_PATTERN)

        Path pathForFile1Pattern = createNewFile(EXTENSION_OF_FILE1_PATTERN)
        Path pathForFile2Pattern = createNewFile(EXTENSION_OF_FILE2_PATTERN)

        Map<Pattern, List<Path>> expectedResults = new HashMap<>()
        expectedResults.put(CORRECT_FILE1_PATTERN, Arrays.asList(pathForFile1Pattern))
        expectedResults.put(CORRECT_FILE2_PATTERN, Arrays.asList(pathForFile2Pattern))

        when:
        FilesByPatternsSearcher searcher = new FilesByPatternsSearcher(directory, patterns)

        then:
        searcher.getResults() == expectedResults
    }

    def "Should correct find only one file"() {
        given:
        File directory = dirPath.toFile()
        List<Pattern> patterns = Arrays.asList(CORRECT_FILE1_PATTERN, CORRECT_FILE2_PATTERN)

        Path pathForFile1Pattern = createNewFile(EXTENSION_OF_FILE1_PATTERN)

        Map<Pattern, List<Path>> expectedResults = new HashMap<>()
        expectedResults.put(CORRECT_FILE1_PATTERN, Arrays.asList(pathForFile1Pattern))
        expectedResults.put(CORRECT_FILE2_PATTERN, new ArrayList<Path>())

        when:
        FilesByPatternsSearcher searcher = new FilesByPatternsSearcher(directory, patterns)

        then:
        searcher.getResults() == expectedResults
    }

    def "Should correct find zero files"() {
        given:
        File directory = dirPath.toFile()
        List<Pattern> patterns = Arrays.asList(CORRECT_FILE1_PATTERN, CORRECT_FILE2_PATTERN)

        Map<Pattern, List<Path>> expectedResults = new HashMap<>()
        expectedResults.put(CORRECT_FILE1_PATTERN, new ArrayList<Path>())
        expectedResults.put(CORRECT_FILE2_PATTERN, new ArrayList<Path>())

        when:
        FilesByPatternsSearcher searcher = new FilesByPatternsSearcher(directory, patterns)

        then:
        searcher.getResults() == expectedResults
    }

    def createNewFile(String extension) {
        String fileName = new Random().nextInt().toString()
        File file1Created = new File(dirPath.toString() + "/" + fileName + extension)
        file1Created.createNewFile()
        return file1Created.toPath()
    }

    def isEachMapTheSame(Map<Pattern, List<Path>> map1, Map<Pattern, List<Path>> map2) {
        if (isEachSetTheSame(map1.keySet(), map2.keySet())) {
            for (Pattern pattern : map1.keySet()) {
                List<Path> paths1 = map1.get(pattern)
                List<Path> paths2 = map2.get(pattern)
                if (!isEachListTheSame(paths1, paths2))
                    return false
            }
            return true
        }
        return false
    }

    def isEachSetTheSame(Set<Pattern> set1, Set<Pattern> set2) {
        return set1.size() == set2.size() &&
                set1.containsAll(set2) &&
                set2.containsAll(set1)
    }

    def isEachListTheSame(List<Path> list1, List<Path> list2) {
        return list1.size() == list2.size() &&
                list1.containsAll(list2) &&
                list2.containsAll(list1)
    }
}
