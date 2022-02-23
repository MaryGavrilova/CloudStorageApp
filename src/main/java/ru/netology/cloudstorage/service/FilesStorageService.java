package ru.netology.cloudstorage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudstorage.dto.CloudFileInfo;
import ru.netology.cloudstorage.dto.CloudFileName;
import ru.netology.cloudstorage.exception.DataValidationException;
import ru.netology.cloudstorage.exception.CloudFileNotFoundException;
import ru.netology.cloudstorage.model.*;
import ru.netology.cloudstorage.repository.FilesRepository;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilesStorageService {

    private final FilesRepository filesRepository;

    // creating file object and saving it in database
    @Transactional
    public boolean storeFile(String fileName, MultipartFile multipartFile) throws IOException {
        User user = getCurrentUser();
        if (filesRepository.findCloudFileByUserAndFilename(user, fileName).isPresent()) {
            throw new DataValidationException("Error store file: Incorrect file name, file this such name already exists");
        }
        filesRepository.save(CloudFile.builder()
                .filename(fileName)
                .originalFilename(multipartFile.getOriginalFilename())
                .size(multipartFile.getSize())
                .contentType(multipartFile.getContentType())
                .bytes(multipartFile.getBytes())
                .user(user)
                .build());
        return true;
    }

    // finding file object in database and deleting it
    @Transactional
    public boolean deleteFile(String fileName) {
        User user = getCurrentUser();
        CloudFile cloudFile = filesRepository.findCloudFileByUserAndFilename(user, fileName)
                .orElseThrow(() -> new CloudFileNotFoundException("Error delete file: Incorrect file name, file is not found"));
        filesRepository.deleteById(cloudFile.getId());
        return true;
    }

    // finding file object in database and returning it
    public byte[] getFile(String fileName) {
        User user = getCurrentUser();
        CloudFile cloudFile = filesRepository.findCloudFileByUserAndFilename(user, fileName)
                .orElseThrow(() -> new CloudFileNotFoundException("Error get file: Incorrect file name, file is not found"));
        return cloudFile.getBytes();
    }

    // finding file object in database anf editing its name
    @Transactional
    public boolean editFileName(String currentFileName, CloudFileName newCloudFileName) {
        User user = getCurrentUser();
        CloudFile cloudFile = filesRepository.findCloudFileByUserAndFilename(user, currentFileName)
                .orElseThrow(() -> new CloudFileNotFoundException("Error edit file: Incorrect file name, file is not found"));
        if (filesRepository.findCloudFileByUserAndFilename(user, newCloudFileName.getFilename()).isPresent()) {
            throw new DataValidationException("Error edit file: Incorrect new file name, file this such name already exists");
        }
        filesRepository.editFileName(newCloudFileName.getFilename(), cloudFile.getId());
        return true;
    }

    // finding all file objects and returning list of files' information
    public List<CloudFileInfo> getInfoAboutAllFiles(int limit) {
        User user = getCurrentUser();
        List<CloudFileInfo> info = filesRepository.findAllByUser(user);
        if (info.size() <= limit) {
            return info;
        } else {
            return info.subList(0, limit);
        }
    }

    // getting from the context user object and returning it
    public User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}


