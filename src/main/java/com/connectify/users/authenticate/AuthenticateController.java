package com.connectify.users.authenticate;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthenticateController {

    AuthenticateService authenticateService;

    public AuthenticateController(AuthenticateService authenticateService){
        this.authenticateService = authenticateService;
    }

    @GetMapping(path = "/confirm")
    public ResponseEntity<?> confirmToken(@RequestParam("token") String token){

        return authenticateService.confirmToken(token);
    }

    @GetMapping(path = "/validate")
    public ResponseEntity<?> validateEmailToken(@RequestParam("token") String token){
        return authenticateService.validateEmailToken(token);
    }

    @PutMapping("/profile-settings/email")
    public ResponseEntity<?> sendEmailVerification(@RequestBody String email, HttpServletRequest request){
        return authenticateService.sendEmailVerification(email, request);
    }

    @PostMapping("/resend-code")
    public ResponseEntity<?> resend(@RequestBody String email){
        return authenticateService.resendToken(email);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody RegisterRequest signUp) {
        return authenticateService.signup(signUp);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest login) {
        return authenticateService.login(login);
    }

    @Scheduled(fixedRate = (1000 * 60 * 30))
    public void deleteUnauthenticatedAccounts() {
        authenticateService.deleteUnauthenticatedAccounts();
    }

    @Scheduled(fixedRate = (1000 * 60 * 60 * 24))
    public void invalidateEmailReverificationTokens(){
        authenticateService.invalidateEmailReverificationTokens();
    }
}
