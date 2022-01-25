package ru.netology.cloudstorage.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudstorage.exception.InputDataException;
import ru.netology.cloudstorage.exception.InvalidCredentialsException;
import ru.netology.cloudstorage.exception.NotExistException;
import ru.netology.cloudstorage.exception.UnauthorizedException;
import ru.netology.cloudstorage.model.*;
import ru.netology.cloudstorage.service.CloudStorageService;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;


@RestController
@Validated
@RequestMapping("/cloud")
public class CloudStorageController {
    private final CloudStorageService cloudStorageService;

    public CloudStorageController(CloudStorageService cloudStorageService) {
        this.cloudStorageService = cloudStorageService;
    }

    //ВОПРОС: нужно ли каждый раз при вводе логина и пароля задавать новый токен автоматически
    // или можно просто старый активировать? здесь этот момент принципиальный?

    // вход
    @PostMapping("/login")
    public AuthorizationToken login(@Valid @RequestBody Identity identity) {
        return cloudStorageService.login(identity);
    }

    // выход
    @PostMapping("/logout")
    public void logout(@RequestHeader("auth-token") String authToken) {
        cloudStorageService.logout(authToken);
    }

    // загрузка файла в облако
    @PostMapping(path = "/file", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public void uploadFile(@RequestHeader("auth-token") String authToken,
                           @RequestParam("filename") @NotBlank String fileName,
                           @RequestParam("file") @NotNull MultipartFile file) throws IOException {
        cloudStorageService.storeFile(authToken, fileName, file);
    }

    // удаление файла в облаке
    @DeleteMapping("/file")
    public void deleteFile(@RequestHeader("auth-token") String authToken,
                           @RequestParam("filename") @NotBlank String fileName) {
        cloudStorageService.deleteFile(authToken, fileName);
    }

    // скачивание файла из облака
    @GetMapping(path = "/file", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public @ResponseBody
    byte[] downloadFile(@RequestHeader("auth-token") String authToken,
                        @RequestParam("filename") @NotBlank String fileName) {
        return cloudStorageService.getFile(authToken, fileName);
    }

    // изменение имени файла в облаке
    @PutMapping("/file")
    public void editFileName(@RequestHeader("auth-token") String authToken,
                             @RequestParam("filename") @NotBlank String currentFileName,
                             @Valid @RequestBody CloudFileName newCloudFileName) {
        cloudStorageService.editFileName(authToken, currentFileName, newCloudFileName);
    }

    // получение списка файлов загруженных в облако
    @GetMapping("/list")
    public List<CloudFileInfo> getInfoAboutAllFiles(@RequestHeader("auth-token") String authToken,
                                                    @RequestParam("limit") @Min(1) Integer limit) {
        return cloudStorageService.getInfoAboutAllFiles(authToken, limit);
    }

    //ВОПРОС: в спецификации в случае возникновения ошибки,
    // она должна быть обернута в объект с полями message и id (строка 253-260),
    // правильно ли я поступила, создав класс CloudStorageError, для этих целей
    // или можно было обойтись Response Entity?

    // ОБРАБОТКА ИСКЛЮЧЕНИЙ

    // POST /login - 400 ошибка – возникает, когда по логину и паролю не найден пользователь в базе
    @ExceptionHandler(InvalidCredentialsException.class)
    CloudStorageError handleInvalidCredentialsException(InvalidCredentialsException e) {
        return new CloudStorageError(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    //ALL REQUEST - 401 ошибка – возникает, когда по токену не найден пользователь базе или указан неактивный токен
    @ExceptionHandler(UnauthorizedException.class)
    CloudStorageError handleUnauthorizedException(UnauthorizedException e) {
        return new CloudStorageError(e.getMessage(), HttpStatus.UNAUTHORIZED.value());
    }

    //ALL REQUEST - 400 ошибка - возникает, когда данные отправленные на сервер не соответствуют требованиям,
    // предъявляемым к формату данных
    @ExceptionHandler(ConstraintViolationException.class)
    CloudStorageError handleConstraintViolationException(ConstraintViolationException e) {
        return new CloudStorageError("Error input data: " + e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    CloudStorageError handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return new CloudStorageError("Error input data: " + e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    CloudStorageError handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        return new CloudStorageError("Error input data: " + e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    CloudStorageError handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        return new CloudStorageError("Error input data: " + e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(InputDataException.class)
    CloudStorageError handleInputDataException(InputDataException e) {
        return new CloudStorageError("Error input data: " + e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    // DELETE /file, PUT /file, GET /file, GET /list - 500 ошибка - возникает, когда файл c указанными параметрами не существует в базе
    @ExceptionHandler(NotExistException.class)
    CloudStorageError handleNotExistException(NotExistException e) {
        return new CloudStorageError(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
