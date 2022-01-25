package ru.netology.cloudstorage.exception;

public class NotExistException extends RuntimeException {

    public NotExistException(String msg) {
        super(msg);
    }
}
