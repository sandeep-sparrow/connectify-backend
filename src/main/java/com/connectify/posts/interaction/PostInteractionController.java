package com.connectify.posts.interaction;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PostInteractionController {

    private final PostInteractionService postInteractionService;


    @PostMapping("/posts/{postId}")
    public ResponseEntity<?> postInteraction(@PathVariable("postId") Long postId, @RequestBody PostInteractionModel postInteraction, HttpServletRequest request){
       return postInteractionService.postInteraction(postId, postInteraction, request);
    }

    @GetMapping("post-interactions/{postId}")
    public ResponseEntity<?> getPostInteraction(@PathVariable("postId") Long postId, HttpServletRequest request){
        System.out.println("test");
        return postInteractionService.getPostInteraction(postId, request);
    }

    @GetMapping("likes/{postId}")
    public ResponseEntity<?> getPostLikes(@PathVariable("postId") Long postId){
        return postInteractionService.getPostLikes(postId);
    }
}
