package pl.filipiak.jakub.training.fileintegration.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import pl.filipiak.jakub.training.fileintegration.config.properties.helpers.AbstractDirConfigProperties;

@ConfigurationProperties(prefix = "user-variables.integration.directories-scan-configs.config1")
public class Dir1ConfigProperties extends AbstractDirConfigProperties {

}
