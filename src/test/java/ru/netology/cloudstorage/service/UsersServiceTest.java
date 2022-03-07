package ru.netology.cloudstorage.service;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.netology.cloudstorage.dto.Identity;
import ru.netology.cloudstorage.exception.DataValidationException;
import ru.netology.cloudstorage.model.User;
import ru.netology.cloudstorage.repository.UsersRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.netology.cloudstorage.service.UsersService.DEFAULT_ROLE;

@RunWith(MockitoJUnitRunner.class)
public class UsersServiceTest {
    @InjectMocks
    UsersService usersService;

    @Mock
    UsersRepository usersRepository;
    @Mock
    PasswordEncoder passwordEncoder;

    static Identity testIdentity;
    static User testUser;

    @BeforeClass
    public static void beforeAll() {
        testIdentity = new Identity();
        testIdentity.setLogin("login");
        testIdentity.setPassword("password");

        testUser = User.builder()
                .username(testIdentity.getLogin())
                .password("$2a$10$tWBB8RARlm4ELSlOVuzwwOgo.gzaSSbCZOR7gMLmT6jhuyjrTMseC")
                .role(DEFAULT_ROLE)
                .build();
    }

    @Test
    public void createUserAccount_success_case_check_result() {
        Mockito.when(usersRepository.findByUsername(testIdentity.getLogin())).thenReturn(Optional.empty());
        Mockito.when(passwordEncoder.encode("password")).thenReturn("$2a$10$tWBB8RARlm4ELSlOVuzwwOgo.gzaSSbCZOR7gMLmT6jhuyjrTMseC");
        Mockito.when(usersRepository.save(testUser)).thenReturn(testUser);

        User result = usersService.createUserAccount(testIdentity);

        Assertions.assertEquals(testUser, result);
    }

    @Test
    public void createUserAccount_success_case_check_exception() {
        Mockito.when(usersRepository.findByUsername(testIdentity.getLogin())).thenReturn(Optional.empty());
        Mockito.when(passwordEncoder.encode("password")).thenReturn("$2a$10$tWBB8RARlm4ELSlOVuzwwOgo.gzaSSbCZOR7gMLmT6jhuyjrTMseC");
        Mockito.when(usersRepository.save(testUser)).thenReturn(testUser);

        assertDoesNotThrow(() -> {
            usersService.createUserAccount(testIdentity);
        });
    }

    @Test
    public void createUserAccount_error_case_check_DataValidationException() {
        Mockito.when(usersRepository.findByUsername(testIdentity.getLogin())).thenReturn(Optional.of(testUser));

        assertThrows(DataValidationException.class,
                () -> {
                    usersService.createUserAccount(testIdentity);
                });
    }
}
