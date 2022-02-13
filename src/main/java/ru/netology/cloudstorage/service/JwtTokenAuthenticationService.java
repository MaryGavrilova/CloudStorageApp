package ru.netology.cloudstorage.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SecurityException;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.netology.cloudstorage.model.BlackListToken;
import ru.netology.cloudstorage.model.User;
import ru.netology.cloudstorage.repository.BlackListTokensRepository;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@RequiredArgsConstructor
@Service
public class JwtTokenAuthenticationService {
    private static final Logger LOGGER = Logger.getLogger(JwtTokenAuthenticationService.class);

    private static final String SECRET = "asdfSFS34wfsdfsdfSDSD32dfsddDDerQSNCK34SOWEK5354fdgdf4";
    private static final Key KEY = new SecretKeySpec(Base64.getDecoder().decode(SECRET),
            SignatureAlgorithm.HS256.getJcaName());
    private static final int TOKEN_EXPIRATION_PERIOD = 30 * 60 * 1000; // 30 min
    private final BlackListTokensRepository blackListTokensRepository;

    // generating new token for user
    public String generateToken(User user) {
        return Jwts.builder()
                .claim("username", user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_PERIOD))
                .signWith(KEY)
                .compact();
    }

    // parsing token
    public Jws<Claims> parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token);
    }

    // getting username from token
    public String getUsername(String token) {
        return (String) parseToken(token).getBody().get("username");
    }

    public Date getExpirationDate(String token) {
        return parseToken(token).getBody().getExpiration();
    }

    // checking token for absence in black list and possibility of decryption
    public boolean validateToken(String token) {
        try {
            if (isTokenInBlackList(token)) {
                return false;
            }
            parseToken(token);
            return true;
        } catch (SecurityException | MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            LOGGER.error(e.getMessage());
        }
        return false;
    }

    // in case of logout - adding token in black list
    @Transactional
    public boolean addTokenInBlackList(String token) {
        BlackListToken blackListToken = BlackListToken.builder().token(token).build();
        blackListTokensRepository.save(blackListToken);
        return true;
    }

    // checking token for presence in black list
    public boolean isTokenInBlackList(String token) {
        return !blackListTokensRepository.findAllByToken(token).isEmpty();
    }
}
