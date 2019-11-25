package pl.filipiak.jakub.training.fileintegration.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@ConfigurationProperties(prefix = "user-variables.integration.files")
@Validated
public class FileIntegrationProperties {

    @NotBlank
    private String storageDirectory;
    @NotBlank
    private String acceptPattern;
    @NotBlank
    private String metadataKeyPrefix;

    public String getStorageDirectory() {
        return storageDirectory;
    }

    public void setStorageDirectory(String storageDirectory) {
        this.storageDirectory = storageDirectory;
    }

    public String getAcceptPattern() {
        return acceptPattern;
    }

    public void setAcceptPattern(String acceptPattern) {
        this.acceptPattern = acceptPattern;
    }

    public String getMetadataKeyPrefix() {
        return metadataKeyPrefix;
    }

    public void setMetadataKeyPrefix(String metadataKeyPrefix) {
        this.metadataKeyPrefix = metadataKeyPrefix;
    }
}
