package com.videopostingsystem.videopostingsystem.users;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticateRequest {

    private UserRepository userRepository;

    public AuthenticateRequest(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<String> signUp(@RequestBody AuthenticateModel signUp, HttpSession session){
        if (userRepository.findById(signUp.username()).isEmpty()){
            if (signUp.username().length() < 8 || signUp.password().length() < 8){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Credentials are not long enough");
            }
            Users users = new Users(signUp.username(), signUp.password());
            userRepository.save(users);
            session.setAttribute("loggedInUser", users.getUsername());
            return ResponseEntity.ok().body("successfully created user!");
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
    }
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthenticateModel login, HttpSession session) {
        if (userRepository.findById(login.username()).isPresent()) {
            Users user = userRepository.getReferenceById(login.username());
            if (login.password().equals(user.getPassword())) {
                // Set the user as logged into the session
                session.setAttribute("loggedInUser", login.username());

                return ResponseEntity.ok("Login successful");

            } else {
                return ResponseEntity.badRequest().body("Incorrect password");
            }
        } else {
            return ResponseEntity.badRequest().body("Incorrect username");
        }
    }
}
