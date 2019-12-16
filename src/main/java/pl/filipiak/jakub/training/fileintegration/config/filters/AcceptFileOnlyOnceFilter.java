package pl.filipiak.jakub.training.fileintegration.config.filters;

import org.springframework.integration.file.filters.AbstractPersistentAcceptOnceFileListFilter;
import org.springframework.integration.metadata.ConcurrentMetadataStore;

import java.io.File;

public class AcceptFileOnlyOnceFilter extends AbstractPersistentAcceptOnceFileListFilter<File> {

    public AcceptFileOnlyOnceFilter(ConcurrentMetadataStore metadataStore, String keyPrefix) {
        super(metadataStore, keyPrefix);
        this.setFlushOnUpdate(true);
    }

    @Override
    protected long modified(File file) {
        return file.lastModified();
    }

    @Override
    protected String fileName(File file) {
        return file.getAbsolutePath();
    }

    @Override
    protected boolean fileStillExists(File file) {
        return file.exists();
    }

    @Override
    protected boolean isEqual(File file, String value) {
        // Do not check if file is modified.
        // Return true means, that old and new file content are the same,
        // so file modifications are ignored.
        // If file was accepted in any time, it will never be accepted again,
        // even if it has been modified after acceptance.
        return true;
    }
}
