package com.videopostingsystem.videopostingsystem.users;

import com.videopostingsystem.videopostingsystem.posts.Post;
import com.videopostingsystem.videopostingsystem.posts.PostRepository;
import com.videopostingsystem.videopostingsystem.posts.interaction.PostInteractionRepository;
import com.videopostingsystem.videopostingsystem.posts.interaction.PostInteractions;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthenticateService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostInteractionRepository postInteractionRepository;
    public AuthenticateService(UserRepository userRepository, PostRepository postRepository, PostInteractionRepository postInteractionRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.postInteractionRepository = postInteractionRepository;
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

    public ResponseEntity<?> deleteAccount(HttpSession session){
        String user = (String) session.getAttribute("loggedInUser");
        if (session.getAttribute("loggedInUser") != null && userRepository.findById(user).isPresent()){
            List<PostInteractions> postInteractions = postInteractionRepository.findByUsers(user);
            for (PostInteractions currPostInteraction : postInteractions){
                postInteractionRepository.deleteById(currPostInteraction.getPostID()+"_"+currPostInteraction.getUsers());
            }
            List<Post> posts = postRepository.findByUsers(user);
            for (Post currPost : posts){
                postRepository.deleteById(currPost.getId());
            }
            userRepository.deleteById((String) session.getAttribute("loggedInUser"));
            return ResponseEntity.ok("Successfully deleted account. We're sad to see you go, " + user + "!");
        }
        else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
    }

    public ResponseEntity<?> deleteAccountAdmin(String user, HttpSession session){
        String adminAccount = (String) session.getAttribute("loggedInUser");
        if (adminAccount != null && userRepository.findById(adminAccount).isPresent()){
            if (userRepository.findById(adminAccount).get().getType().equals("ADMIN")){
                List<PostInteractions> postInteractions = postInteractionRepository.findByUsers(user);
                for (PostInteractions currPostInteraction : postInteractions){
                    postInteractionRepository.deleteById(currPostInteraction.getPostID()+"_"+currPostInteraction.getUsers());
                }
                List<Post> posts = postRepository.findByUsers(user);
                for (Post currPost : posts){
                    postRepository.deleteById(currPost.getId());
                }
                userRepository.deleteById(user);
                return ResponseEntity.ok("Successfully deleted user " + user);
            }
            else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to delete this users account!");
        }
        else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
    }
}
