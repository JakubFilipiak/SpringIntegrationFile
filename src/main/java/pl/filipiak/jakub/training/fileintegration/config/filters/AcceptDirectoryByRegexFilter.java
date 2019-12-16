package pl.filipiak.jakub.training.fileintegration.config.filters;

import lombok.RequiredArgsConstructor;
import org.springframework.integration.file.filters.AbstractDirectoryAwareFileListFilter;

import java.io.File;

@RequiredArgsConstructor
public class AcceptDirectoryByRegexFilter extends AbstractDirectoryAwareFileListFilter<File> {

    private final String dirPattern;

    @Override
    protected boolean isDirectory(File file) {
        return file != null && file.isDirectory();
    }

    @Override
    public boolean accept(File file) {
        return isDirectory(file) && file.getName().matches(dirPattern);
    }
}
