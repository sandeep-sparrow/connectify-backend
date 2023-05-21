package com.connectify.posts.interaction;

import com.connectify.posts.Post;
import com.connectify.posts.PostRepository;
import com.connectify.recommendSystem.FeedService;
import com.connectify.users.UserRepository;
import com.connectify.users.Users;
import com.connectify.users.config.JwtService;
import com.connectify.users.notification.NotificationService;
import com.google.gson.Gson;
import com.connectify.users.notification.NotificationType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@AllArgsConstructor
public class PostInteractionService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostInteractionRepository postInteractionRepository;
    @Autowired
    private final NotificationService notificationService;
    private final JwtService jwtService;



    public ResponseEntity<?> postInteraction(Long postId, PostInteractionModel postInteraction, HttpServletRequest request){
        String username = jwtService.getUsername(request);
        PostInteractions newPostInteraction;
        Users user = userRepository.findById(username).get();
        if (postRepository.findById(postId).isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid post ID provided");
        }
        Post post = postRepository.findById(postId).get();
        if (!postInteraction.liked() && !postInteraction.bookmark()) {
            if (postInteractionRepository.findById(postId + "_" + username).isEmpty()){
                return null;
            }
            else {
                if (postInteractionRepository.findById(postId + "_" + username).get().isLiked()){
                    notificationService.removeNotification(user, post.getUsers(), NotificationType.LIKE, postId);
                }
                postInteractionRepository.deleteById(postId + "_" + username);
                return ResponseEntity.ok().body("removed post " + postId + " from users interactions");
            }
        }
        if (postInteractionRepository.findById(postId + "_" + username).isEmpty()){
            if (postInteraction.liked()){
                notificationService.setNotification(user, post.getUsers(), NotificationType.LIKE, postId);
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
                String category = Objects.requireNonNull(FeedService.likedCategories(username, userRepository, postRepository, postInteractionRepository))[0];
                assert category != null;
                if (!category.equals(user.getTopCategory())){
                    user.setTopCategory(category);
                    userRepository.save(user);
                }

            }
        }
        return ResponseEntity.ok(newPostInteraction);


    }

    public ResponseEntity<?> getPostInteraction(Long postId, HttpServletRequest request) {
        String username = jwtService.getUsername(request);
        Gson gson = new Gson();
        PostInteractionModel postInteractionModel = new PostInteractionModel(false, false);

        String json = gson.toJson(postInteractionModel);
        if (postInteractionRepository.findById(postId + "_" + username).isEmpty()){
            return ResponseEntity.ok().body(json);
        }
            boolean liked = postInteractionRepository.findById(postId + "_" + username).get().isLiked();
            boolean bookmarked = postInteractionRepository.findById(postId + "_" + username).get().isBookmark();
            postInteractionModel = new PostInteractionModel(liked, bookmarked);
            json = gson.toJson(postInteractionModel);
            return ResponseEntity.ok().body(json);
    }

    public ResponseEntity<?> getPostLikes(Long postId) {
        return ResponseEntity.ok(postInteractionRepository.findAllLikedByPostID(postId).size());
    }
}
