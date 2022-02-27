package ru.netology.cloudstorage.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SecurityException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import ru.netology.cloudstorage.model.User;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static ru.netology.cloudstorage.service.UsersService.DEFAULT_ROLE;

@RunWith(MockitoJUnitRunner.class)
public class TestJwtTokenAuthenticationService {
    private static final String TEST_SECRET = "asdfSFS34wfsdfsdfSDSD32dfsddDDerQSNCK34SOWEK5354fdgdf4";
    private static final Key TEST_KEY = new SecretKeySpec(Base64.getDecoder().decode(TEST_SECRET),
            SignatureAlgorithm.HS256.getJcaName());
    private static final int TEST_TOKEN_EXPIRATION_PERIOD = 10 * 60 * 1000;

    @Spy
    @InjectMocks
    JwtTokenAuthenticationService jwtTokenAuthenticationService;

    static User testUser;
    static String testToken;

    @BeforeClass
    public static void beforeAll() {
        testUser = User.builder()
                .username("login")
                .password("$2a$10$tWBB8RARlm4ELSlOVuzwwOgo.gzaSSbCZOR7gMLmT6jhuyjrTMseC")
                .role(DEFAULT_ROLE)
                .build();

        testToken = Jwts.builder()
                .claim("username", testUser.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + TEST_TOKEN_EXPIRATION_PERIOD))
                .signWith(TEST_KEY)
                .compact();
    }

    @Test
    public void generateToken_success_case_check_exception() {
        assertDoesNotThrow(() -> {
            jwtTokenAuthenticationService.generateToken(testUser);
        });
    }

    @Test
    public void parseToken_success_case_check_exception() {
        assertDoesNotThrow(() -> {
            jwtTokenAuthenticationService.parseToken(testToken);
        });
    }

    @Test
    public void getUsername_success_case() {
        String expected = "login";
        String result = jwtTokenAuthenticationService.getUsername(testToken);

        Assertions.assertEquals(expected, result);
    }

    @Test
    public void validateToken_success_case() {
        Assertions.assertTrue(jwtTokenAuthenticationService.validateToken(testToken));
    }

    @Test
    public void validateToken_error_case_MalformedJwtException_real_data() {
        String invalidToken =
                "JhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6ImRlbmlzIiwiaWF0IjoxNjQ1MTIzNzI3LCJleHAiOjE2NDUxMjU1Mjd9.1bM5mxZhcc_8Uq5lNZdCzDtnOqWx7_PhwF3gr4J91sA";
        Assertions.assertFalse(jwtTokenAuthenticationService.validateToken(invalidToken));
    }

    @Test
    public void validateToken_error_case_ExpiredJwtException_real_data() {
        String invalidToken =
                "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6ImRlbmlzIiwiaWF0IjoxNjQ1MTIzNzI3LCJleHAiOjE2NDUxMjU1Mjd9.1bM5mxZhcc_8Uq5lNZdCzDtnOqWx7_PhwF3gr4J91sA";
        Assertions.assertFalse(jwtTokenAuthenticationService.validateToken(invalidToken));
    }

    @Test
    public void validateToken_error_case_SecurityException_real_data() {
        String invalidToken = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJlNjc4ZjIzMzQ3ZTM0MTBkYjdlNjg3Njc4MjNiMmQ3MCIsImlhdCI6MTQ2NjYzMzMxNywibmJmIjoxNDY2NjMzMzE3LCJleHAiOjE0NjY2MzY5MTd9.rgx_o8VQGuDa2AqCHSgVOD5G68Ld_YYM7N7THmvLIKc";
        Assertions.assertFalse(jwtTokenAuthenticationService.validateToken(invalidToken));
    }

    @Test
    public void validateToken_error_case_MalformedJwtException_mock_data() {
        Mockito.when(jwtTokenAuthenticationService.parseToken(testToken)).thenThrow(MalformedJwtException.class);
        Assertions.assertFalse(jwtTokenAuthenticationService.validateToken(testToken));
    }

    @Test
    public void validateToken_error_case_SecurityException_mock_data() {
        Mockito.when(jwtTokenAuthenticationService.parseToken(testToken)).thenThrow(SecurityException.class);
        Assertions.assertFalse(jwtTokenAuthenticationService.validateToken(testToken));
    }

    @Test
    public void validateToken_error_case_UnsupportedJwtException_mock_data() {
        Mockito.when(jwtTokenAuthenticationService.parseToken(testToken)).thenThrow(UnsupportedJwtException.class);
        Assertions.assertFalse(jwtTokenAuthenticationService.validateToken(testToken));
    }

    @Test
    public void validateToken_error_case_IllegalArgumentException_mock_data() {
        Mockito.when(jwtTokenAuthenticationService.parseToken(testToken)).thenThrow(IllegalArgumentException.class);
        Assertions.assertFalse(jwtTokenAuthenticationService.validateToken(testToken));
    }

    @Test
    public void validateToken_error_case_ExpiredJwtException_mock_data() {
        Mockito.when(jwtTokenAuthenticationService.parseToken(testToken)).thenThrow(ExpiredJwtException.class);
        Assertions.assertFalse(jwtTokenAuthenticationService.validateToken(testToken));
    }
}
