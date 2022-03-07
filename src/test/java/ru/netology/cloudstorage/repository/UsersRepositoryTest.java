package ru.netology.cloudstorage.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import ru.netology.cloudstorage.model.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = {BaseRepositoryTest.Initializer.class})
@Sql("classpath:users-test-data.sql")
public class UsersRepositoryTest extends BaseRepositoryTest {

    @Autowired
    UsersRepository usersRepository;

    @Test
    public void findByUsername_success_case_check_result_found() {
        User expected = User.builder()
                .id(1)
                .username("login")
                .password("password")
                .role("ROLE_USER")
                .build();

        User result = usersRepository.findByUsername("login").orElseThrow();

        assertThat(result).usingRecursiveComparison()
                .ignoringFields("id", "files").isEqualTo(expected);
    }

    @Test
    public void findByUsername_success_case_check_result_not_found() {
        Optional<User> result = usersRepository.findByUsername("not exist user");

        assertThat(result).isEmpty();
    }


    @Test
    public void saveUser_success_case_check_result() {
        User expected = User.builder()
                .username("login2")
                .password("password2")
                .role("ROLE_USER")
                .build();

        User result = usersRepository.save(expected);

        assertThat(result).usingRecursiveComparison()
                .ignoringFields("id", "files").isEqualTo(expected);
    }

    @Test
    public void saveUser_error_case_DataIntegrityViolationException_null_field() {
        User user = User.builder()
                .username("login2")
                .password(null)
                .role("ROLE_USER")
                .build();

        assertThrows(DataIntegrityViolationException.class,
                () -> {
                    usersRepository.save(user);
                });
    }

    @Test
    public void saveUser_error_case_DataIntegrityViolationException_duplicate() {
        User user = User.builder()
                .username("login")
                .password("password")
                .role("ROLE_USER")
                .build();

        assertThrows(DataIntegrityViolationException.class,
                () -> {
                    usersRepository.save(user);
                });
    }

    @Test
    public void saveUser_error_case_InvalidDataAccessApiUsageException_null_user() {
        assertThrows(InvalidDataAccessApiUsageException.class,
                () -> {
                    usersRepository.save(null);
                });
    }
}
