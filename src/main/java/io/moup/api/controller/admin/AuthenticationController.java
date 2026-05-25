package io.moup.api.controller.admin;

import io.moup.api.service.admin.AuthenticationService;
import io.moup.api.view.LoginView;
import io.moup.api.view.TokenView;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("admin")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("auth/login")
    public TokenView login(@RequestBody LoginView loginView) {
        return authenticationService.login(loginView.getPassword());
    }

    @PostMapping("generate/password")
    public String generatePassword(@RequestBody LoginView loginView) {
        return authenticationService.generatePassword(loginView.getPassword());
    }
}
