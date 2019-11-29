package pl.filipiak.jakub.training.fileintegration.config.filters;

import org.springframework.integration.file.filters.AbstractDirectoryAwareFileListFilter;

import java.io.File;

public class AcceptDirectoryByRegexFilter extends AbstractDirectoryAwareFileListFilter<File> {

    private final String DIR_PATTERN;

    public AcceptDirectoryByRegexFilter(String directoryPattern) {
        DIR_PATTERN = directoryPattern;
    }

    @Override
    protected boolean isDirectory(File file) {
        return file != null && file.isDirectory();
    }

    @Override
    public boolean accept(File file) {
        return isDirectory(file) && file.getName().matches(DIR_PATTERN);
    }
}
