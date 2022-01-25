package ru.netology.cloudstorage.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudstorage.exception.InputDataException;
import ru.netology.cloudstorage.exception.InvalidCredentialsException;
import ru.netology.cloudstorage.exception.NotExistException;
import ru.netology.cloudstorage.exception.UnauthorizedException;
import ru.netology.cloudstorage.model.*;
import ru.netology.cloudstorage.repository.FilesRepository;
import ru.netology.cloudstorage.repository.UsersRepository;

import java.io.IOException;
import java.util.List;

@Service
public class CloudStorageService {

    private final UsersRepository usersRepository;
    private final FilesRepository filesRepository;

    public CloudStorageService(UsersRepository usersRepository, FilesRepository filesRepository) {
        this.usersRepository = usersRepository;
        this.filesRepository = filesRepository;
    }

    @Transactional
    public AuthorizationToken login(Identity identity) {
        User user = usersRepository.findUserByIdentityLoginAndIdentityPassword(identity.getLogin(), identity.getPassword())
                .orElseThrow(() -> new InvalidCredentialsException("Bad credentials"));
        usersRepository.activateUserToken(user.getId());
        return user.getAuthorizationToken();
    }

    @Transactional
    public boolean logout(String authToken) {
        User user = getUserByToken(authToken);
        usersRepository.deactivateUserToken(user.getId());
        return true;
    }

    @Transactional
    public boolean storeFile(String authToken, String fileName, MultipartFile multipartFile) throws IOException {
        User user = getUserByToken(authToken);
        if (filesRepository.findCloudFileByUserAndFilename(user, fileName).isPresent()) {
            throw new InputDataException("Incorrect file name, file this such name already exists");
        }
        filesRepository.save(CloudFile.builder()
                .filename(fileName)
                .originalFilename(multipartFile.getOriginalFilename())
                .size((int) multipartFile.getSize())
                //ВОПРОС: по спецификации требуется возращать список файлов (GET /list 229-230 строка спецификации)
                // с указанием размеров файлов size в int, может в спецификации ошибка?
                // логичнее было бы везде использовать long
                .contentType(multipartFile.getContentType())
                .bytes(multipartFile.getBytes())
                .user(user)
                .build());
        return true;
    }

    @Transactional
    public boolean deleteFile(String authToken, String fileName) {
        User user = getUserByToken(authToken);
        CloudFile cloudFile = filesRepository.findCloudFileByUserAndFilename(user, fileName)
                .orElseThrow(() -> new NotExistException("Error delete file: Incorrect file name, file is not found"));
        filesRepository.deleteById(cloudFile.getId());
        return true;
    }


    public byte[] getFile(String authToken, String fileName) {
        User user = getUserByToken(authToken);
        CloudFile cloudFile = filesRepository.findCloudFileByUserAndFilename(user, fileName)
                .orElseThrow(() -> new NotExistException("Error upload file: Incorrect file name, file is not found"));
        return cloudFile.getBytes();
    }

    @Transactional
    public boolean editFileName(String authToken, String currentFileName, CloudFileName newCloudFileName) {
        User user = getUserByToken(authToken);
        CloudFile cloudFile = filesRepository.findCloudFileByUserAndFilename(user, currentFileName)
                .orElseThrow(() -> new NotExistException("Error edit file: Incorrect file name, file is not found"));
        if (filesRepository.findCloudFileByUserAndFilename(user, newCloudFileName.getName()).isPresent()) {
            throw new InputDataException("Incorrect file name, file this such name already exists");
        }
        filesRepository.editFileName(newCloudFileName.getName(), cloudFile.getId());
        return true;
    }

    public List<CloudFileInfo> getInfoAboutAllFiles(String authToken, int limit) {
        User user = getUserByToken(authToken);
        List<CloudFileInfo> info = filesRepository.findAllByUser(user);
        if (info.isEmpty()) {
            throw new NotExistException("Error getting file list: there are not files");
            //ВОПРОС: в спецификации говорится об ошибке с кодом 500 (245-250 строка), я подумала,
            //что она может возникать в случае получения из базы пустого списка, правильно я интерпертировала?
        }
        if (info.size() <= limit) {
            return info;
        } else {
            return info.subList(0, limit);
        }
    }

    public User getUserByToken(String authToken) {
        User user = usersRepository.findUserByAuthorizationTokenAuthToken(authToken)
                .orElseThrow(() -> new UnauthorizedException("Incorrect token, user is unauthorized"));
        if (user.isTokenActive()) {
            return user;
        } else {
            throw new UnauthorizedException("Inactive token, user is unauthorized");
        }
    }
}


