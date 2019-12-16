package pl.filipiak.jakub.training.fileintegration.utils;

import org.springframework.integration.metadata.ConcurrentMetadataStore;
import org.springframework.integration.metadata.PropertiesPersistingMetadataStore;
import org.springframework.stereotype.Component;

@Component
public class MetadataStoreFactory {

    public ConcurrentMetadataStore createPropertiesPersistingMetadataStore(String metadataStoreFileName) {
        PropertiesPersistingMetadataStore metadataStore = new PropertiesPersistingMetadataStore();
        metadataStore.setFileName(metadataStoreFileName + ".properties");
        return metadataStore;
    }
}
