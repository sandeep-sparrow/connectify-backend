package com.videopostingsystem.videopostingsystem.posts.interaction;

import com.videopostingsystem.videopostingsystem.posts.Post;
import com.videopostingsystem.videopostingsystem.posts.PostRepository;
import com.videopostingsystem.videopostingsystem.recommendSystem.UserLikeFrequencies;
import com.videopostingsystem.videopostingsystem.users.UserRepository;
import com.videopostingsystem.videopostingsystem.users.Users;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PostInteractionService {
    PostRepository postRepository;
    UserRepository userRepository;
    PostInteractionRepository postInteractionRepository;


    public ResponseEntity<?> postInteraction(Long postId, PostInteractionModel postInteraction, HttpSession session){
        String loggedInUser = (String) session.getAttribute("loggedInUser");
        PostInteractions newPostInteraction;
        if (loggedInUser == null || userRepository.findById(loggedInUser).isEmpty()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }
        Users user = userRepository.findById(loggedInUser).get();
        if (postRepository.findById(postId).isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid post ID provided");
        }
        if (!postInteraction.liked() && !postInteraction.bookmark()) {
            return null;
        }
        Post post = postRepository.findById(postId).get();
        if (postInteractionRepository.findById(postId + "_" + loggedInUser).isEmpty()){
            if (postInteraction.liked()){
                post.setLikes(post.getLikes()+1);
            }
            if (postInteraction.bookmark()){
                post.setBookmarks(post.getBookmarks()+1);
            }
        }
        newPostInteraction = new PostInteractions(postId, user, postInteraction.liked(), postInteraction.bookmark());
        postInteractionRepository.save(newPostInteraction);
        postRepository.save(post);
        if (user.getTopCategory() == null){
            user.setTopCategory(post.getCategory());
            userRepository.save(user);
        }
        else {
            if (!user.getTopCategory().equals(post.getCategory())){
                String category = UserLikeFrequencies.mostLikedCategory(session, userRepository, postRepository, postInteractionRepository);
                assert category != null;
                if (!category.equals(user.getTopCategory())){
                    user.setTopCategory(category);
                    userRepository.save(user);
                }

            }
        }
        return ResponseEntity.ok(newPostInteraction);


    }
}
