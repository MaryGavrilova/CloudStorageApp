package ru.netology.cloudstorage.exception;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException(String msg) {
        super(msg);
    }
}
