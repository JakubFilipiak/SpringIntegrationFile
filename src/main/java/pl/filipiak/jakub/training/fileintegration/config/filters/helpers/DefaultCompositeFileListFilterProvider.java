package pl.filipiak.jakub.training.fileintegration.config.filters.helpers;

import lombok.RequiredArgsConstructor;
import org.springframework.integration.file.filters.AbstractDirectoryAwareFileListFilter;
import org.springframework.integration.file.filters.AbstractPersistentAcceptOnceFileListFilter;
import org.springframework.integration.file.filters.CompositeFileListFilter;
import org.springframework.integration.metadata.ConcurrentMetadataStore;
import pl.filipiak.jakub.training.fileintegration.config.filters.AcceptDirectoryByRegexFilter;
import pl.filipiak.jakub.training.fileintegration.config.filters.AcceptEveryDirectoryFilter;
import pl.filipiak.jakub.training.fileintegration.config.filters.AcceptFileOnlyOnceFilter;

import java.io.File;

@RequiredArgsConstructor
public class DefaultCompositeFileListFilterProvider {

    private final boolean dirValidationEnabled;
    private final String dirPattern;
    private final String metadataKeyPrefix;

    public CompositeFileListFilter<File> createDefaultCompositeFileListFilter(ConcurrentMetadataStore metadataStore) {
        CompositeFileListFilter<File> filters = new CompositeFileListFilter<>();
        filters.addFilter(createAcceptDirectoriesFilter());
        filters.addFilter(createAcceptFilesOnlyOnceFilter(metadataStore));
        return filters;
    }

    private AbstractDirectoryAwareFileListFilter<File> createAcceptDirectoriesFilter() {
        if (dirValidationEnabled && dirPattern != null && !dirPattern.isEmpty())
            return new AcceptDirectoryByRegexFilter(dirPattern);
        return new AcceptEveryDirectoryFilter();
    }

    private AbstractPersistentAcceptOnceFileListFilter<File> createAcceptFilesOnlyOnceFilter(
            ConcurrentMetadataStore metadataStore) {
        return new AcceptFileOnlyOnceFilter(metadataStore, metadataKeyPrefix);
    }
}
