package com.videopostingsystem.videopostingsystem.posts.interaction;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:63343")
public class PostInteractionController {

    private final PostInteractionService postInteractionService;

    public PostInteractionController(PostInteractionService postInteractionService){
        this.postInteractionService = postInteractionService;
    }


    @PostMapping("/posts/{postId}")
    public ResponseEntity<?> postInteraction(@PathVariable("postId") Long postId, @RequestBody PostInteractionModel postInteraction, HttpSession session){
       return postInteractionService.postInteraction(postId, postInteraction, session);
    }

    @GetMapping("post-interactions/{postId}")
    public ResponseEntity<?> getPostInteraction(@PathVariable("postId") Long postId, HttpSession session){
        System.out.println("test");
        return postInteractionService.getPostInteraction(postId, session);
    }
}
