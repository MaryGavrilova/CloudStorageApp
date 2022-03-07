package ru.netology.cloudstorage.service;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudstorage.dto.CloudFileInfo;
import ru.netology.cloudstorage.dto.CloudFileName;
import ru.netology.cloudstorage.exception.CloudFileNotFoundException;
import ru.netology.cloudstorage.exception.DataValidationException;
import ru.netology.cloudstorage.model.CloudFile;
import ru.netology.cloudstorage.model.User;
import ru.netology.cloudstorage.repository.FilesRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static ru.netology.cloudstorage.service.UsersService.DEFAULT_ROLE;

@RunWith(MockitoJUnitRunner.class)
public class FilesStorageServiceTest {
    @InjectMocks
    FilesStorageService filesStorageService;

    @Mock
    FilesRepository filesRepository;
    @Mock
    Authentication authentication;
    @Mock
    SecurityContext securityContext;

    static User testUser;
    static String testFileName;
    static MultipartFile testMultipartFile;
    static CloudFile testCloudFile;
    static CloudFileName testNewCloudFileName;
    static List<CloudFileInfo> testInfoList;

    @BeforeClass
    public static void beforeAll() throws IOException {
        testUser = User.builder()
                .username("login")
                .password("$2a$10$tWBB8RARlm4ELSlOVuzwwOgo.gzaSSbCZOR7gMLmT6jhuyjrTMseC")
                .role(DEFAULT_ROLE)
                .build();

        testFileName = "file_name";

        testMultipartFile = new MockMultipartFile(testFileName, "original_file_name", "content_type", "file_content".getBytes());

        testCloudFile = CloudFile.builder()
                .filename(testFileName)
                .originalFilename(testMultipartFile.getOriginalFilename())
                .size(testMultipartFile.getSize())
                .contentType(testMultipartFile.getContentType())
                .bytes(testMultipartFile.getBytes())
                .user(testUser)
                .build();

        testNewCloudFileName = new CloudFileName("new_file_name");

        testInfoList = new ArrayList<>();
        testInfoList.add(new CloudFileInfo("file_name_1", 10));
        testInfoList.add(new CloudFileInfo("file_name_2", 20));
        testInfoList.add(new CloudFileInfo("file_name_3", 30));
    }

    @Test
    public void getCurrentUser_success_case_check_result() {
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(testUser);

        User expected = testUser;
        User result = filesStorageService.getCurrentUser();

        Assertions.assertEquals(expected, result);
    }

    @Test
    public void storeFile_success_case_check_result() throws IOException {
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(testUser);
        Mockito.when(filesRepository.findCloudFileByUserAndFilename(testUser, testFileName)).thenReturn(Optional.empty());
        Mockito.when(filesRepository.save(testCloudFile)).thenReturn(testCloudFile);

        CloudFile result = filesStorageService.storeFile(testFileName, testMultipartFile);

        Assertions.assertEquals(testCloudFile, result);
    }

    @Test
    public void storeFile_success_case_check_exception() {
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(testUser);
        Mockito.when(filesRepository.findCloudFileByUserAndFilename(testUser, testFileName)).thenReturn(Optional.empty());
        Mockito.when(filesRepository.save(testCloudFile)).thenReturn(testCloudFile);

        assertDoesNotThrow(() -> {
            filesStorageService.storeFile(testFileName, testMultipartFile);
        });
    }

    @Test
    public void storeFile_error_case_check_DataValidationException() {
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(testUser);
        Mockito.when(filesRepository.findCloudFileByUserAndFilename(testUser, testFileName)).thenReturn(Optional.of(testCloudFile));

        assertThrows(DataValidationException.class,
                () -> {
                    filesStorageService.storeFile(testFileName, testMultipartFile);
                });
    }

    @Test
    public void deleteFile_success_case_check_result() {
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(testUser);
        Mockito.when(filesRepository.findCloudFileByUserAndFilename(testUser, testFileName)).thenReturn(Optional.of(testCloudFile));
        Mockito.doNothing().when(filesRepository).deleteById(testCloudFile.getId());

        CloudFile result = filesStorageService.deleteFile(testFileName);

        Assertions.assertEquals(testCloudFile, result);
    }

    @Test
    public void deleteFile_success_case_check_exception() {
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(testUser);
        Mockito.when(filesRepository.findCloudFileByUserAndFilename(testUser, testFileName)).thenReturn(Optional.of(testCloudFile));
        Mockito.doNothing().when(filesRepository).deleteById(testCloudFile.getId());

        assertDoesNotThrow(() -> {
            filesStorageService.deleteFile(testFileName);
        });
    }

    @Test
    public void deleteFile_error_case_check_CloudFileNotFoundException() {
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(testUser);
        Mockito.when(filesRepository.findCloudFileByUserAndFilename(testUser, testFileName)).thenReturn(Optional.empty());

        assertThrows(CloudFileNotFoundException.class,
                () -> {
                    filesStorageService.deleteFile(testFileName);
                });
    }

    @Test
    public void getFile_success_case_check_result() {
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(testUser);
        Mockito.when(filesRepository.findCloudFileByUserAndFilename(testUser, testFileName)).thenReturn(Optional.of(testCloudFile));

        byte[] expected = testCloudFile.getBytes();
        byte[] result = filesStorageService.getFile(testFileName);

        Assertions.assertEquals(expected, result);
    }

    @Test
    public void getFile_success_case_check_exception() {
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(testUser);
        Mockito.when(filesRepository.findCloudFileByUserAndFilename(testUser, testFileName)).thenReturn(Optional.of(testCloudFile));

        assertDoesNotThrow(() -> {
            filesStorageService.getFile(testFileName);
        });
    }

    @Test
    public void getFile_error_case_check_CloudFileNotFoundException() {
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(testUser);
        Mockito.when(filesRepository.findCloudFileByUserAndFilename(testUser, testFileName)).thenReturn(Optional.empty());

        assertThrows(CloudFileNotFoundException.class,
                () -> {
                    filesStorageService.getFile(testFileName);
                });
    }

    @Test
    public void editFileName_success_case_check_result() {
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(testUser);
        Mockito.when(filesRepository.findCloudFileByUserAndFilename(testUser, testFileName)).thenReturn(Optional.of(testCloudFile));
        Mockito.when(filesRepository.findCloudFileByUserAndFilename(testUser, testNewCloudFileName.getFilename())).thenReturn(Optional.empty());
        Mockito.doNothing().when(filesRepository).editFileName(testNewCloudFileName.getFilename(), testCloudFile.getId());

        String expected = testNewCloudFileName.getFilename();
        String result = filesStorageService.editFileName(testFileName, testNewCloudFileName);

        Assertions.assertEquals(expected, result);
    }

    @Test
    public void editFileName_success_case_check_exception() {
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(testUser);
        Mockito.when(filesRepository.findCloudFileByUserAndFilename(testUser, testFileName)).thenReturn(Optional.of(testCloudFile));
        Mockito.when(filesRepository.findCloudFileByUserAndFilename(testUser, testNewCloudFileName.getFilename())).thenReturn(Optional.empty());
        Mockito.doNothing().when(filesRepository).editFileName(testNewCloudFileName.getFilename(), testCloudFile.getId());

        assertDoesNotThrow(() -> {
            filesStorageService.editFileName(testFileName, testNewCloudFileName);
        });
    }

    @Test
    public void editFileName_success_case_check_CloudFileNotFoundException() {
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(testUser);
        Mockito.when(filesRepository.findCloudFileByUserAndFilename(testUser, testFileName)).thenReturn(Optional.empty());

        assertThrows(CloudFileNotFoundException.class,
                () -> {
                    filesStorageService.editFileName(testFileName, testNewCloudFileName);
                });
    }

    @Test
    public void editFileName_success_case_check_DataValidationException() {
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(testUser);
        Mockito.when(filesRepository.findCloudFileByUserAndFilename(testUser, testFileName)).thenReturn(Optional.of(testCloudFile));
        Mockito.when(filesRepository.findCloudFileByUserAndFilename(testUser, testNewCloudFileName.getFilename())).thenReturn(Optional.of(testCloudFile));

        assertThrows(DataValidationException.class,
                () -> {
                    filesStorageService.editFileName(testFileName, testNewCloudFileName);
                });
    }

    @Test
    public void getInfoAboutAllFiles_success_case_check_result_allFilesListSize_less_than_limit() {
        int limit = 4;

        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(testUser);
        Mockito.when(filesRepository.findAllByUser(testUser)).thenReturn(testInfoList);

        List<CloudFileInfo> result = filesStorageService.getInfoAboutAllFiles(limit);

        assertTrue(testInfoList.size() == result.size() && testInfoList.containsAll(result) && result.containsAll(testInfoList));
    }

    @Test
    public void getInfoAboutAllFiles_success_case_check_result_allFilesListSize_equals_limit() {
        int limit = 3;

        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(testUser);
        Mockito.when(filesRepository.findAllByUser(testUser)).thenReturn(testInfoList);

        List<CloudFileInfo> result = filesStorageService.getInfoAboutAllFiles(limit);

        assertTrue(testInfoList.size() == result.size() && testInfoList.containsAll(result) && result.containsAll(testInfoList));
    }

    @Test
    public void getInfoAboutAllFiles_success_case_check_result_allFilesListSize_more_than_limit() {
        int limit = 2;

        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(testUser);
        Mockito.when(filesRepository.findAllByUser(testUser)).thenReturn(testInfoList);

        List<CloudFileInfo> expected = new ArrayList<>();
        expected.add(new CloudFileInfo("file_name_1", 10));
        expected.add(new CloudFileInfo("file_name_2", 20));
        List<CloudFileInfo> result = filesStorageService.getInfoAboutAllFiles(limit);

        assertTrue(expected.size() == result.size() && expected.containsAll(result) && result.containsAll(expected));
    }

    @Test
    public void getInfoAboutAllFiles_success_case_check_result_emptyFilesList() {
        List<CloudFileInfo> testEmptyInfoList = Collections.emptyList();
        int limit = 3;

        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(testUser);
        Mockito.when(filesRepository.findAllByUser(testUser)).thenReturn(testEmptyInfoList);

        List<CloudFileInfo> result = filesStorageService.getInfoAboutAllFiles(limit);

        assertEquals(0, result.size());
    }
}
