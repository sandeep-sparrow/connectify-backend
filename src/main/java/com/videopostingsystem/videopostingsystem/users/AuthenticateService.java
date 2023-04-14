package com.videopostingsystem.videopostingsystem.users;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AuthenticateService {
    private final UserRepository userRepository;

    public AuthenticateService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ResponseEntity<?> signup(AuthenticateModel signUp, HttpSession session){
        if (signUp != null) {
            if (signUp.username() != null && signUp.password() != null) {
                if (userRepository.findById(signUp.username()).isEmpty()) {
                    if (signUp.username().length() < 8 || signUp.password().length() < 8) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Credentials are not long enough");
                    }
                    Users user;
                    String type = "user";
                    if (signUp.security_clearance() != null) {
                        if (signUp.security_clearance().equals(CONSTANTS.security_clearance)) {
                            user = new Users(signUp.username(), signUp.password(), "ADMIN");
                            user.setTopCategory("blank");
                            type = "admin";
                        } else {
                            user = new Users(signUp.username(), signUp.password(), "USER");
                            user.setTopCategory("blank");
                        }
                    } else {
                        user = new Users(signUp.username(), signUp.password(), "USER");
                        user.setTopCategory("blank");
                    }
                    userRepository.save(user);
                    session.setAttribute("loggedInUser", user.getUsername());
                    return ResponseEntity.ok().body("successfully created " + type + " account!");
                }
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
            } else {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Must provide username and password");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Must provide username and password");
        }
    }

    public ResponseEntity<?> login(AuthenticateModel login, HttpSession session){
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
