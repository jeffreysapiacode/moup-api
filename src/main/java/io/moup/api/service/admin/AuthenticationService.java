package io.moup.api.service.admin;

import io.moup.api.view.TokenView;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    @Value("${app.admin.auth.login.password}")
    private String adminPassword;

    private final static String USERNAME = "admin";

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthenticationService(PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public TokenView login(String password) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(USERNAME, password));
        if (authentication.isAuthenticated()) {
            return TokenView.builder()
                    .token(jwtService.generateToken(USERNAME))
                    .build();
        } else {
            throw new UsernameNotFoundException("Invalid user request!");
        }
    }

    public String generatePassword(String password) {
        return passwordEncoder.encode(password);
    }
}
