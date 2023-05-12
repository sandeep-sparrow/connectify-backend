package com.connectify.posts.comments;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/comment/{postID}")
    public ResponseEntity<?> createComment(@PathVariable("postID") Long postID, @RequestBody String content, HttpServletRequest request){
        return commentService.createComment(postID, content, request);
    }

    @GetMapping("/comment/{postID}")
    public ResponseEntity<?> getPostComments(@PathVariable("postID") Long postID){
        return commentService.getPostComments(postID);
    }
}