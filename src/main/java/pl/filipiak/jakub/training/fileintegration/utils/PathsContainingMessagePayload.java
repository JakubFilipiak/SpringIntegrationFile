package pl.filipiak.jakub.training.fileintegration.utils;

import lombok.Data;
import lombok.NonNull;

import java.nio.file.Path;

@Data
public class PathsContainingMessagePayload {

    @NonNull
    private String configId;
    private Path file1Path;
    private Path file2Path;
}
