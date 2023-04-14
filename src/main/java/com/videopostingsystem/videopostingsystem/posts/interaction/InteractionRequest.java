package com.videopostingsystem.videopostingsystem.posts.interaction;

import com.videopostingsystem.videopostingsystem.posts.Post;
import com.videopostingsystem.videopostingsystem.posts.PostRepository;
import com.videopostingsystem.videopostingsystem.recommendSystem.UserLikeFrequencies;
import com.videopostingsystem.videopostingsystem.users.UserRepository;
import com.videopostingsystem.videopostingsystem.users.Users;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InteractionRequest {

    PostRepository postRepository;
    UserRepository userRepository;
    PostInteractionRepository postInteractionRepository;

    public InteractionRequest(PostRepository postRepository, UserRepository userRepository, PostInteractionRepository postInteractionRepository){
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.postInteractionRepository = postInteractionRepository;
    }


    @PostMapping("/posts/{postId}")
    public ResponseEntity<?> postInteraction(@PathVariable("postId") Long postId, @RequestBody PostInteractionModel postInteraction, HttpSession session){
        String loggedInUser = (String) session.getAttribute("loggedInUser");
        PostInteractions newPostInteraction;
        if (loggedInUser != null){
            Users user = userRepository.findById(loggedInUser).get();
            if (postRepository.findById(postId).isPresent()){
                if (postInteraction.liked() || postInteraction.bookmark()) {
                    Post post = postRepository.findById(postId).get();
                    if (postInteractionRepository.findById(postId + "_" + loggedInUser).isEmpty()){
                        if (postInteraction.liked()){
                            post.setLikes(post.getLikes()+1);
                        }
                        if (postInteraction.bookmark()){
                            post.setBookmarks(post.getBookmarks()+1);
                        }
                    }
                    newPostInteraction = new PostInteractions(postId, loggedInUser, postInteraction.liked(), postInteraction.bookmark());
                    postInteractionRepository.save(newPostInteraction);
                    postRepository.save(post);
                    if (user.getTopCategory() == null){
                        user.setTopCategory(post.getCategory());
                        userRepository.save(user);
                    }
                    else {
                        if (!user.getTopCategory().equals(post.getCategory())){
                            String category = UserLikeFrequencies.mostLikedCategory(session, userRepository, postRepository, postInteractionRepository);
                            if (!category.equals(user.getTopCategory())){
                                user.setTopCategory(category);
                                userRepository.save(user);
                            }

                        }
                    }
                    return ResponseEntity.ok(newPostInteraction);
                }
                else return null;
            }
            else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid post ID provided");
        }
        else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
    }
}
