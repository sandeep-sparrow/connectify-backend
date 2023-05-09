package com.videopostingsystem.videopostingsystem.posts.comments;

import com.google.gson.Gson;
import com.videopostingsystem.videopostingsystem.posts.PostRepository;
import com.videopostingsystem.videopostingsystem.users.UserRepository;
import com.videopostingsystem.videopostingsystem.users.Users;
import com.videopostingsystem.videopostingsystem.users.config.JwtService;
import com.videopostingsystem.videopostingsystem.users.notification.NotificationService;
import com.videopostingsystem.videopostingsystem.users.notification.NotificationType;
import jakarta.servlet.http.HttpServletRequest;
import com.videopostingsystem.videopostingsystem.posts.Post;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class CommentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    @Autowired
    private final NotificationService notificationService;
    private final JwtService jwtService;

    public ResponseEntity<?> createComment(Long postID, String content, HttpServletRequest request) {
        String username = jwtService.getUsername(request);
        if (postID == null || postRepository.findById(postID).isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Post does not exist");
        }
        Post post = postRepository.findById(postID).get();
        Users user = userRepository.findById(username).get();
        Users postOwner = post.getUsers();
        Comment comment = new Comment(post, user, content);
        commentRepository.save(comment);
        notificationService.setNotification(user, postOwner, NotificationType.COMMENT, comment.getId());
        return ResponseEntity.ok("Created comment!");
    }

    public ResponseEntity<?> getPostComments(Long postID){
        if (postID == null || postRepository.findById(postID).isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Post does not exist");
        }
        Post post = postRepository.findById(postID).get();
        List<Comment> postComments = commentRepository.findAllByPost(post);
        List<CommentResponseModel> formattedComments = new ArrayList<>();
        for (Comment comment : postComments){
            formattedComments.add(new CommentResponseModel(comment.getUser().getUsername(), comment.getContent(), comment.getCreationDate()));
        }
        Gson gson = new Gson();
        String json = gson.toJson(formattedComments);
        return ResponseEntity.ok(json);
    }
}