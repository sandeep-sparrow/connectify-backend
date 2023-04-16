package com.videopostingsystem.videopostingsystem.posts.interaction;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PostInteractionController {

    private final PostInteractionService postInteractionService;

    public PostInteractionController(PostInteractionService postInteractionService){
        this.postInteractionService = postInteractionService;
    }


    @PostMapping("/posts/{postId}")
    public ResponseEntity<?> postInteraction(@PathVariable("postId") Long postId, @RequestBody PostInteractionModel postInteraction, HttpSession session){
       return postInteractionService.postInteraction(postId, postInteraction, session);
    }
}
