package pl.filipiak.jakub.training.fileintegration.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import pl.filipiak.jakub.training.fileintegration.config.properties.helpers.AbstractDirConfigProperties;

@ConfigurationProperties(prefix = "user-variables.integration.directories-scan-configs.config3")
public class Dir3ConfigProperties extends AbstractDirConfigProperties {

}
