package ru.netology.cloudstorage.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import ru.netology.cloudstorage.dto.CloudFileInfo;
import ru.netology.cloudstorage.model.CloudFile;
import ru.netology.cloudstorage.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = {BaseRepositoryTest.Initializer.class})
public class FilesRepositoryTest extends BaseRepositoryTest {

    @Autowired
    FilesRepository filesRepository;
    @Autowired
    UsersRepository usersRepository;

    @Test
    @Sql("classpath:users-files-test-data1.sql")
    public void findCloudFileByUserAndFilename_success_case_check_result_found() {
        User user = usersRepository.findByUsername("login1").orElseThrow();
        CloudFile expected = CloudFile.builder()
                .filename("file name1")
                .originalFilename("original file name1")
                .size(10)
                .contentType("content type")
                .bytes("test1".getBytes())
                .user(user)
                .build();

        String filename = "file name1";

        CloudFile result = filesRepository.findCloudFileByUserAndFilename(user, filename).orElseThrow();

        assertThat(result).usingRecursiveComparison()
                .ignoringFields("id").isEqualTo(expected);
    }

    @Test
    @Sql("classpath:users-files-test-data1.sql")
    public void findCloudFileByUserAndFilename_success_case_check_result_not_found() {
        User user = usersRepository.findByUsername("login1").orElseThrow();
        Optional<CloudFile> result = filesRepository.findCloudFileByUserAndFilename(user, "not exist file");

        assertThat(result).isEmpty();
    }

    @Test
    @Sql("classpath:users-files-test-data1.sql")
    public void findAllByUser_success_case_check_result_found() {
        User user = usersRepository.findByUsername("login1").orElseThrow();
        List<CloudFileInfo> expected = new ArrayList<>();
        expected.add(new CloudFileInfo("file name1", 10));
        expected.add(new CloudFileInfo("file name2", 20));

        List<CloudFileInfo> result = filesRepository.findAllByUser(user);

        assertTrue(expected.size() == result.size() && expected.containsAll(result) && result.containsAll(expected));
    }

    @Test
    @Sql("classpath:users-files-test-data2.sql")
    public void findAllByUser_success_case_check_result_found_empty_list() {
        User user = usersRepository.findByUsername("login2").orElseThrow();
        List<CloudFileInfo> result = filesRepository.findAllByUser(user);

        assertEquals(0, result.size());
    }

    @Test
    @Sql("classpath:users-files-test-data2.sql")
    public void findAllByUser_error_case_check_result_found_empty_list_for_not_exist_user() {
        User user = User.builder()
                .id(10)
                .username("not exist name")
                .password("not exist password")
                .role("not exist role")
                .files(Collections.emptyList())
                .build();

        List<CloudFileInfo> result = filesRepository.findAllByUser(user);
        assertEquals(0, result.size());
    }

    @Test
    @Sql("classpath:users-files-test-data3.sql")
    public void saveCloudFile_success_case_check_result() {
        User user = usersRepository.findByUsername("login3").orElseThrow();
        CloudFile expected = CloudFile.builder()
                .filename("file name")
                .originalFilename("original file name")
                .size(10)
                .contentType("content type")
                .bytes("test".getBytes())
                .user(user)
                .build();

        CloudFile result = filesRepository.save(expected);

        assertThat(result).usingRecursiveComparison()
                .ignoringFields("id").isEqualTo(expected);
    }

    @Test
    @Sql("classpath:users-files-test-data3.sql")
    public void saveCloudFile_error_case_DataIntegrityViolationException_null_field() {
        User user = usersRepository.findByUsername("login3").orElseThrow();
        CloudFile cloudFile = CloudFile.builder()
                .filename(null)
                .originalFilename("original file name1")
                .size(10)
                .contentType("content type")
                .bytes("test1".getBytes())
                .user(user)
                .build();

        assertThrows(DataIntegrityViolationException.class,
                () -> {
                    filesRepository.save(cloudFile);
                });
    }

    @Test
    @Sql("classpath:users-files-test-data3.sql")
    public void saveCloudFile_error_case_JpaSystemException_negative_file_size() {
        User user = usersRepository.findByUsername("login3").orElseThrow();
        CloudFile cloudFile = CloudFile.builder()
                .filename("file name2")
                .originalFilename("original file name2")
                .size(-10)
                .contentType("content type")
                .bytes("test2".getBytes())
                .user(user)
                .build();

        assertThrows(JpaSystemException.class,
                () -> {
                    filesRepository.save(cloudFile);
                });
    }

    @Test
    @Sql("classpath:users-files-test-data3.sql")
    public void saveCloudFile_error_case_InvalidDataAccessApiUsageException_null_file() {
        assertThrows(InvalidDataAccessApiUsageException.class,
                () -> {
                    filesRepository.save(null);
                });
    }

    @Test
    @Sql("classpath:users-files-test-data4.sql")
    public void deleteById_success_case_check_result() {
        User user = usersRepository.findByUsername("login4").orElseThrow();
        CloudFile cloudFile = filesRepository.findCloudFileByUserAndFilename(user, "file name").orElseThrow();
        long id = cloudFile.getId();

        filesRepository.deleteById(id);

        assertThat(filesRepository.findById(id)).isEmpty();
    }

    @Test
    @Sql("classpath:users-files-test-data4.sql")
    public void deleteById_error_case_InvalidDataAccessApiUsageException_null_id() {
        assertThrows(InvalidDataAccessApiUsageException.class,
                () -> {
                    filesRepository.deleteById(null);
                });
    }

    @Test
    @Sql("classpath:users-files-test-data4.sql")
    public void deleteById_error_case_EmptyResultDataAccessException_not_exist_id() {
        assertThrows(EmptyResultDataAccessException.class,
                () -> {
                    filesRepository.deleteById(100L);
                });
    }

    @Test
    @Sql("classpath:users-files-test-data5.sql")
    public void editFileName_success_case_check_result() {
        User user = usersRepository.findByUsername("login5").orElseThrow();
        CloudFile cloudFile = filesRepository.findCloudFileByUserAndFilename(user, "file name").orElseThrow();
        long id = cloudFile.getId();
        String newFileName = "new file name";

        filesRepository.editFileName(newFileName, id);
        CloudFile result = filesRepository.findById(id).orElseThrow();

        assertEquals(result.getFilename(), newFileName);
    }

    @Test
    @Sql("classpath:users-files-test-data5.sql")
    public void editFileName_error_case_DataIntegrityViolationException_null_new_file_name() {
        User user = usersRepository.findByUsername("login5").orElseThrow();
        CloudFile cloudFile = filesRepository.findCloudFileByUserAndFilename(user, "file name").orElseThrow();
        long id = cloudFile.getId();

        assertThrows(DataIntegrityViolationException.class,
                () -> {
                    filesRepository.editFileName(null, id);
                });
    }
}
