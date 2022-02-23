package ru.netology.cloudstorage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import ru.netology.cloudstorage.dto.AuthorizationToken;
import ru.netology.cloudstorage.dto.Identity;
import ru.netology.cloudstorage.model.User;
import ru.netology.cloudstorage.service.JwtTokenAuthenticationService;
import ru.netology.cloudstorage.service.UsersService;

import javax.validation.Valid;

@RestController
@Validated
//@RequestMapping("/cloud")
@RequiredArgsConstructor
public class UsersAuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenAuthenticationService jwtTokenAuthenticationService;
    private final UsersService usersService;

    // log in cloud storage by login and password
    @PostMapping("/login")
    public AuthorizationToken login(@RequestBody @Valid Identity identity) {

        Authentication authenticate = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(identity.getLogin(), identity.getPassword()));

        User user = (User) authenticate.getPrincipal();

        return new AuthorizationToken(jwtTokenAuthenticationService.generateToken(user));
    }

    // sign in cloud storage by creating login and password
    @PostMapping("/signin")
    public User signin(@RequestBody @Valid Identity identity) {
        return usersService.createUserAccount(identity);
    }
}
