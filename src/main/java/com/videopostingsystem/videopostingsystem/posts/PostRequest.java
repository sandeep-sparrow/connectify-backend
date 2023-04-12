package com.videopostingsystem.videopostingsystem.posts;

import com.videopostingsystem.videopostingsystem.users.UserRepository;
import com.videopostingsystem.videopostingsystem.users.Users;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

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
                if (post.body().length() < 200 && post.body().length() > 10){
                    Users user = userRepository.getReferenceById(loggedInUser);
                    Post newPost = new Post(user.getUsername(), post.title(), post.body(), post.tag());
                    postRepository.save(newPost);
                    return ResponseEntity.ok(newPost);
                }
                else return ResponseEntity.badRequest().body("Body must be 10-200 characters long");

            }
            else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
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

    @GetMapping("/post/{id}")
    public ResponseEntity<?> getPost(@PathVariable("id") Long id, HttpSession session){
        String loggedInUser = (String) session.getAttribute("loggedInUser");
        if (loggedInUser != null){
            if (postRepository.findById(id).isPresent()){
                return ResponseEntity.ok(postRepository.findById(id));
            }
            else return ResponseEntity.badRequest().body("Post ID not valid.");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
    }

    @PutMapping("/post/{id}")
    public ResponseEntity<?> updatePost(@PathVariable("id") Long id, @RequestBody PostModel postModel, HttpSession session){
        String loggedInUser = (String) session.getAttribute("loggedInUser");
        if (loggedInUser != null){
            Optional<Post> post = postRepository.findById(id);
            if (post.isPresent()){
                Post postObj = post.get();
                if (loggedInUser.equals(postObj.getUsers())){
                    if (postModel.body()!= null){
                        if (postModel.body().length() < 200 && postModel.body().length() > 10){
                            postObj.setBody(postModel.body());
                        }
                        else return ResponseEntity.badRequest().body("Body must be 10-200 characters long");
                    }
                    if (postModel.title()!= null){
                        if (postModel.title().length() < 100 && postModel.title().length() > 5){
                            postObj.setTitle(postModel.title());
                        }
                        else return ResponseEntity.badRequest().body("Title must be between 5-100 characters");
                    }
                    if (postModel.tag()!= null){
                        if (postModel.tag().length() < 20){
                            postObj.setTag(postModel.tag());
                        }
                        else return ResponseEntity.badRequest().body("tag cannot be longer than 20 characters");
                    }
                    postObj.setLastModifiedDate(new Date());

                    postRepository.save(postObj);
                    return ResponseEntity.ok(postObj);

                } else return ResponseEntity.badRequest().body("You can only edit your own posts.");
            }
            else return ResponseEntity.badRequest().body("Post ID not valid.");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
    }
    }


