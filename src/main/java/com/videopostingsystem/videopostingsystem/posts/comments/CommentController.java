package com.videopostingsystem.videopostingsystem.posts.comments;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/comment/{postID}")
    public ResponseEntity<?> createComment(@PathVariable("postID") Long postID, @RequestBody String content, HttpSession session){
        return commentService.createComment(postID, content, session);
    }

    @GetMapping("/comment/{postID}")
    public ResponseEntity<?> getPostComments(@PathVariable("postID") Long postID, HttpSession session){
        return commentService.getPostComments(postID, session);
    }
}
