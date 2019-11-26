package pl.filipiak.jakub.training.fileintegration.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ConfigurationProperties(prefix = "user-variables.integration.files")
@Validated
public class FileIntegrationProperties {

    @NotBlank
    private String storageDirectory;
    @NotBlank
    private String acceptPattern;
    @NotBlank
    private String metadataKeyPrefix;
}
