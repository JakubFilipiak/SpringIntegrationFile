package pl.filipiak.jakub.training.fileintegration.utils;

import java.io.IOException;

public class MoreFilesThanExpectedException extends IOException {

    public MoreFilesThanExpectedException(String message) {
        super(message);
    }
}
