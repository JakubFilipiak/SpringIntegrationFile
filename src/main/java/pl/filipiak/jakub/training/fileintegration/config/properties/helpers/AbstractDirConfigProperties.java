package pl.filipiak.jakub.training.fileintegration.config.properties.helpers;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Validated
public abstract class AbstractDirConfigProperties {

    @NotBlank
    protected String storageDirectory;
    @NotBlank
    protected String file1AcceptPattern;
    @NotBlank
    protected String file2AcceptPattern;
    @NotBlank
    protected String metadataKeyPrefix;
    @NotNull
    protected boolean directoriesValidationEnabled;
    protected String directoriesAcceptPattern;
}
