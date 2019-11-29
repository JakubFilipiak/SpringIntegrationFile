package pl.filipiak.jakub.training.fileintegration.config.filters;

import org.springframework.integration.file.filters.AbstractDirectoryAwareFileListFilter;

import java.io.File;

public class AcceptEveryDirectoryFilter extends AbstractDirectoryAwareFileListFilter<File> {

    @Override
    protected boolean isDirectory(File file) {
        return file != null && file.isDirectory();
    }

    @Override
    public boolean accept(File file) {
        return isDirectory(file);
    }
}
