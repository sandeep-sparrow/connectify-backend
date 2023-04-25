package com.videopostingsystem.videopostingsystem.posts.interaction;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.videopostingsystem.videopostingsystem.posts.Post;
import com.videopostingsystem.videopostingsystem.posts.PostRepository;
import com.videopostingsystem.videopostingsystem.recommendSystem.FeedService;
import com.videopostingsystem.videopostingsystem.users.UserRepository;
import com.videopostingsystem.videopostingsystem.users.Users;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Objects;

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
                String category = Objects.requireNonNull(FeedService.likedCategories(session, userRepository, postRepository, postInteractionRepository))[0];
                assert category != null;
                if (!category.equals(user.getTopCategory())){
                    user.setTopCategory(category);
                    userRepository.save(user);
                }

            }
        }
        return ResponseEntity.ok(newPostInteraction);


    }

    public ResponseEntity<?> getPostInteraction(Long postId, String type, HttpSession session) {
        System.out.println("test2");
        System.out.println(type);
        String loggedInUser = (String) session.getAttribute("loggedInUser");
        if (loggedInUser == null || userRepository.findById(loggedInUser).isEmpty()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }
        if (postInteractionRepository.findById(postId + "_" + loggedInUser).isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("User has not interacted with post");
        }
        System.out.println("dddd");
        if (type.equalsIgnoreCase("both")){
            Gson gson = new Gson();
            boolean liked = postInteractionRepository.findById(postId + "_" + loggedInUser).get().isLiked();
            boolean bookmarked = postInteractionRepository.findById(postId + "_" + loggedInUser).get().isBookmark();
            PostInteractionModel postInteractionModel = new PostInteractionModel(liked, bookmarked);
            String json = gson.toJson(postInteractionModel);
            return ResponseEntity.ok().body(json);
        }
        if (type.equalsIgnoreCase("liked")) {
            boolean liked = postInteractionRepository.findById(postId + "_" + loggedInUser).get().isLiked();
            Gson gson = new Gson();
            ResultModel resultModel = new ResultModel(liked);
            //TODO fix this unrecognized json.
            String json = gson.toJson(resultModel);
            return ResponseEntity.ok().body(json);
        } else if (type.equalsIgnoreCase("bookmarked")) {
            boolean bookmarked = postInteractionRepository.findById(postId + "_" + loggedInUser).get().isBookmark();
            return ResponseEntity.ok().body(Collections.singletonMap("result", bookmarked));
        }

        else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Need to input liked or bookmarked");
        }
    }
}
