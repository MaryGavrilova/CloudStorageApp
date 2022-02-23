package ru.netology.cloudstorage.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.netology.cloudstorage.repository.UsersRepository;
import ru.netology.cloudstorage.service.JwtTokenAuthenticationService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenAuthenticationService jwtTokenAuthenticationService;
    private final UsersRepository usersRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        // Get jwt token and validate
        final String token = request.getHeader("auth-token");
        if (token == null || !token.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        final String jwtToken = token.split(" ")[1].trim();
        if (!jwtTokenAuthenticationService.validateToken(jwtToken)) {
            chain.doFilter(request, response);
            return;
        }

        // Get user identity and set it on the spring security context
        UserDetails userDetails = usersRepository
                .findByUsername(jwtTokenAuthenticationService.getUsername(jwtToken))
                .orElse(null);

        UsernamePasswordAuthenticationToken
                authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null,
                userDetails == null ? List.of() : userDetails.getAuthorities()
        );

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }
}
