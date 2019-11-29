package pl.filipiak.jakub.training.fileintegration.config.filters;

import org.springframework.integration.file.filters.AbstractPersistentAcceptOnceFileListFilter;
import org.springframework.integration.metadata.ConcurrentMetadataStore;

import java.io.File;

public class AcceptFileOnlyOnceFilter extends AbstractPersistentAcceptOnceFileListFilter<File> {

    public AcceptFileOnlyOnceFilter(ConcurrentMetadataStore metadataStore, String keyPrefix) {
        super(metadataStore, keyPrefix);
        super.setFlushOnUpdate(true);
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
        return true;
    }
}
