package com.videopostingsystem.videopostingsystem.posts;

import com.videopostingsystem.videopostingsystem.users.UserRepository;
import com.videopostingsystem.videopostingsystem.users.Users;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PostRequest {
    PostRepository postRepository;
    UserRepository userRepository;

    public PostRequest(PostRepository postRepository, UserRepository userRepository){
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/create-post")
    public ResponseEntity<?> createPost(@RequestBody PostModel post, HttpSession session){
        String loggedInUser = (String) session.getAttribute("loggedInUser");
            if (loggedInUser != null){
                if (post.title().length() < 5){
                    return ResponseEntity.badRequest().body("Title must be longer than 5 characters");
                }
                if (post.body().length() < 200){
                    Users user = userRepository.getReferenceById(loggedInUser);
                    Post newPost = new Post(user, post.title(), post.body(), post.tag());
                    postRepository.save(newPost);
                    return ResponseEntity.ok(newPost);
                }

            }
            else return ResponseEntity.badRequest().body("Must be logged in to post.");

            return ResponseEntity.badRequest().body("Body cannot be longer than 200 characters");
        }


    @GetMapping("/posts")
    public ResponseEntity<?> allPosts(HttpSession session) {
        String loggedInUser = (String) session.getAttribute("loggedInUser");
        if (loggedInUser != null) {
            List<Post> posts = postRepository.findAll();
            return ResponseEntity.ok(posts);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }
    }
    }



