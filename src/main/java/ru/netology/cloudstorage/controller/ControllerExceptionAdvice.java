package ru.netology.cloudstorage.controller;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.netology.cloudstorage.exception.DataValidationException;
import ru.netology.cloudstorage.exception.CloudFileNotFoundException;
import ru.netology.cloudstorage.dto.CloudStorageError;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

// handling exceptions
@RestControllerAdvice
public class ControllerExceptionAdvice {

    private static final Logger LOGGER = Logger.getLogger(ControllerExceptionAdvice.class);

    @ExceptionHandler(UsernameNotFoundException.class)
    public CloudStorageError handleUsernameNotFoundException(HttpServletRequest request, UsernameNotFoundException e) {
        LOGGER.error(request.getRequestURI() + ": UsernameNotFoundException: " + e.getMessage());

        CloudStorageError error = new CloudStorageError();
        error.setMessage(e.getMessage());
        error.setId(HttpStatus.BAD_REQUEST.value());
        return error;
    }

    @ExceptionHandler(CloudFileNotFoundException.class)
    public CloudStorageError handleCloudFileNotFoundException(HttpServletRequest request, CloudFileNotFoundException e) {
        LOGGER.error(request.getRequestURI() + ": CloudFileNotFoundException: " + e.getMessage());

        CloudStorageError error = new CloudStorageError();
        error.setMessage(e.getMessage());
        error.setId(HttpStatus.BAD_REQUEST.value());
        return error;
    }

    @ExceptionHandler(DataValidationException.class)
    public CloudStorageError handleDataValidationException(HttpServletRequest request, DataValidationException e) {
        LOGGER.error(request.getRequestURI() + ": DataValidationException: " + e.getMessage());

        CloudStorageError error = new CloudStorageError();
        error.setMessage(e.getMessage());
        error.setId(HttpStatus.BAD_REQUEST.value());
        return error;
    }

    @ExceptionHandler(value = {ConstraintViolationException.class,
            MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class, MethodArgumentNotValidException.class})
    public CloudStorageError handleInputDataException(HttpServletRequest request, Exception e) {
        LOGGER.error(request.getRequestURI() + ": InputDataException: " + e.getMessage());

        CloudStorageError error = new CloudStorageError();
        error.setMessage(e.getMessage());
        error.setId(HttpStatus.BAD_REQUEST.value());
        return error;
    }
}

