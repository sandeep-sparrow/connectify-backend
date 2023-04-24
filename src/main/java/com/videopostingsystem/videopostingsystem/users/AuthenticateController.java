package com.videopostingsystem.videopostingsystem.users;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthenticateController {

    private final AuthenticateService authenticateService;

    public AuthenticateController(AuthenticateService authenticateService) {
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

    @DeleteMapping("/delete-account")
    public ResponseEntity<?> deleteAccount(HttpSession session){
        return authenticateService.deleteAccount(session);
    }

    @DeleteMapping("/delete-account/{user}")
    public ResponseEntity<?> deleteAccountAdmin(@PathVariable("user") String user, HttpSession session){
        return authenticateService.deleteAccountAdmin(user, session);
    }

    @GetMapping(path = "/confirm")
    public ResponseEntity<?> confirmToken(@RequestParam("token") String token){
        System.out.println(token);
        return authenticateService.confirmToken(token);
    }

    @PostMapping("/resend-code")
    public ResponseEntity<?> resend(@RequestBody String email){
        return authenticateService.resendToken(email);
    }

}
