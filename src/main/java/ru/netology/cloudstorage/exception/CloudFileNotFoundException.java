package ru.netology.cloudstorage.exception;

public class CloudFileNotFoundException extends RuntimeException {

    public CloudFileNotFoundException(String msg) {
        super(msg);
    }
}
