package ru.netology.cloudstorage.exception;

public class DataValidationException extends RuntimeException {

    public DataValidationException(String msg) {
        super(msg);
    }
}
