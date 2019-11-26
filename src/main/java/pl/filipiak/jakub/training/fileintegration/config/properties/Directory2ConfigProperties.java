package pl.filipiak.jakub.training.fileintegration.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "user-variables.integration.directories-scan-configs.config2")
public class Directory2ConfigProperties {

    @NotBlank
    private String storageDirectory;
    @NotBlank
    private String fileAcceptPattern;
    @NotBlank
    private String metadataKeyPrefix;
}
