package com.connectify.posts;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping("/create-post")
    public ResponseEntity<?> createPost(@RequestBody PostCreateModel post, HttpServletRequest request){
            return postService.createPost(post, request);
        }


    @GetMapping("/posts")
    public ResponseEntity<?> getPosts(@RequestParam int page) {
        return postService.getPosts(page);
    }

    @GetMapping("/posts/{user}")
    public ResponseEntity<?> getUserPosts(@PathVariable("user") String user, @RequestParam int page){
        return postService.getUserPosts(user, page);
    }

    @GetMapping("/post/{id}")
    public ResponseEntity<?> getPost(@PathVariable("id") Long id){
        return postService.getPost(id);
    }

    @GetMapping("/my-posts")
    public ResponseEntity<?> myPosts(@RequestParam int page, HttpServletRequest request){
        return postService.myPosts(page, request);
    }

    @PutMapping("/post/{id}")
    public ResponseEntity<?> updatePost(@PathVariable("id") Long id, @RequestBody PostCreateModel postCreateModel, HttpServletRequest request){
        return postService.updatePost(id, postCreateModel, request);
    }

    @DeleteMapping("/post/{id}")
    public ResponseEntity<?> deletePost(@PathVariable("id") Long id, HttpServletRequest request){
        return postService.deletePost(id, request);
    }
}



