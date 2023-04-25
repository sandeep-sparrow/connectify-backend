package com.videopostingsystem.videopostingsystem.posts;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:63343")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService){
        this.postService = postService;
    }

    @PostMapping("/create-post")
    public ResponseEntity<?> createPost(@RequestBody PostCreateModel post, HttpSession session){
            return postService.createPost(post, session);
        }


    @GetMapping("/posts")
    public ResponseEntity<?> allPosts(HttpSession session) {
        return postService.allPosts(session);
    }

    @GetMapping("/post/{id}")
    public ResponseEntity<?> getPost(@PathVariable("id") Long id, HttpSession session){
        return postService.getPost(id, session);
    }

    @PutMapping("/post/{id}")
    public ResponseEntity<?> updatePost(@PathVariable("id") Long id, @RequestBody PostCreateModel postCreateModel, HttpSession session){
        return postService.updatePost(id, postCreateModel, session);
    }

    @DeleteMapping("/post/{id}")
    public ResponseEntity<?> deletePost(@PathVariable("id") Long id, HttpSession session){
        return postService.deletePost(id, session);
    }
    }



