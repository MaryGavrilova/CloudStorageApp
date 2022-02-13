package ru.netology.cloudstorage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.netology.cloudstorage.dto.Identity;
import ru.netology.cloudstorage.exception.DataValidationException;
import ru.netology.cloudstorage.model.User;
import ru.netology.cloudstorage.repository.UsersRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsersService {
    public static final String DEFAULT_ROLE = "ROLE_USER";
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    // creating new user account by login and password
    @Transactional
    public User createUserAccount(Identity identity) {
        if (usersRepository.findByUsername(identity.getLogin()).isPresent()) {
            throw new DataValidationException("Error create user account: Incorrect user name, user with such name already exists");
        }
        User user = User.builder()
                .username(identity.getLogin())
                .password(passwordEncoder.encode(identity.getPassword()))
                .role(DEFAULT_ROLE)
                .build();
        usersRepository.save(user);
        return user;
    }
}
