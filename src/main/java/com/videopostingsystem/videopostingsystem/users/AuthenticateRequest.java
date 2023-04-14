package com.videopostingsystem.videopostingsystem.users;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticateRequest {

    private final AuthenticateService authenticateService;

    public AuthenticateRequest(AuthenticateService authenticateService) {
        this.authenticateService = authenticateService;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody AuthenticateModel signUp, HttpSession session) {
        return authenticateService.signup(signUp, session);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticateModel login, HttpSession session) {
        return authenticateService.login(login, session);
    }
}
