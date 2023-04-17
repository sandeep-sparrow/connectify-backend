package com.videopostingsystem.videopostingsystem.posts;

import com.videopostingsystem.videopostingsystem.openapi.OpenAPI;
import com.videopostingsystem.videopostingsystem.posts.interaction.PostInteractionRepository;
import com.videopostingsystem.videopostingsystem.posts.interaction.PostInteractions;
import com.videopostingsystem.videopostingsystem.users.UserRepository;
import com.videopostingsystem.videopostingsystem.users.UserType;
import com.videopostingsystem.videopostingsystem.users.Users;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
@Service
@AllArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostInteractionRepository postInteractionRepository;

    public ResponseEntity<?> createPost(PostCreateModel post, HttpSession session){
        String loggedInUser = (String) session.getAttribute("loggedInUser");
        if (loggedInUser == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }
        if (post.title() == null || post.title().length() < 5 || post.title().length() > 50){
            return ResponseEntity.badRequest().body("Title must be longer than 5 characters and less than 50 characters");
        }
        if (post.body() == null || post.body().length() > 500 || post.body().length() < 10) {
            return ResponseEntity.badRequest().body("Body must be 10-500 characters long");
        }
        Users user = userRepository.getReferenceById(loggedInUser);
        Post newPost = new Post(user, post.title(), post.body());
        String category = OpenAPI.request("categorize this content in 1 of these 20 categories returning only the ONE WORD of the category. " +
                "However, if you view the content as very offensive return the word 'Invalid':" +
                "Technology\n" +
                "Travel\n" +
                "Food\n" +
                "Fashion\n" +
                "Sports\n" +
                "Health\n" +
                "Beauty\n" +
                "Music\n" +
                "Gaming\n" +
                "Finance\n" +
                "Education\n" +
                "Art\n" +
                "Politics\n" +
                "Science\n" +
                "Environment\n" +
                "Literature\n" +
                "Business\n" +
                "Entertainment\n" +
                "Social issues\n" +
                "History" +
                "Miscellaneous" +
                "Here is the content:" +
                post.title() + post.body());
        category = category.toLowerCase();
        if (category.contains("invalid")){
            return ResponseEntity.badRequest().body("Post upload failed. This content does not adhere to our post policies.");
        }
        newPost.setCategory(category);
        postRepository.save(newPost);
        return ResponseEntity.ok(new PostResponseModel(newPost.getId(),
                newPost.getUsers().getUsername(),
                newPost.getTitle(),
                newPost.getBody(),
                newPost.getLikes(),
                newPost.getBookmarks(),
                newPost.getLastModifiedDate(),
                newPost.getCategory()));
    }

    public ResponseEntity<?> allPosts(HttpSession session){
        String loggedInUser = (String) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }

        List<Post> posts = postRepository.findAll();
        List<PostResponseModel> modelPosts = new ArrayList<>();
        for (Post post : posts){
            modelPosts.add(new PostResponseModel(post.getId(),
                    post.getUsers().getUsername(),
                    post.getTitle(), post.getBody(),
                    post.getLikes(), post.getBookmarks(),
                    post.getLastModifiedDate(),
                    post.getCategory()));
        }
        return ResponseEntity.ok(modelPosts);
        }

    public ResponseEntity<?> getPost(Long id, HttpSession session){
        String loggedInUser = (String) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }
        if (postRepository.findById(id).isEmpty()) {
            return ResponseEntity.badRequest().body("Post ID not valid.");
        }
        Post post = postRepository.findById(id).get();
        return ResponseEntity.ok(new PostResponseModel(post.getId(),
                post.getUsers().getUsername(),
                post.getTitle(), post.getBody(),
                post.getLikes(), post.getBookmarks(),
                post.getLastModifiedDate(),
                post.getCategory()));
    }

    public ResponseEntity<?> updatePost(Long id, PostCreateModel postCreateModel, HttpSession session){
        String loggedInUser = (String) session.getAttribute("loggedInUser");
        if (loggedInUser == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }
        Optional<Post> post = postRepository.findById(id);
        if (post.isEmpty()){
            return ResponseEntity.badRequest().body("Post ID not valid.");
        }
        Post postObj = post.get();
        if (!loggedInUser.equals(postObj.getUsers().getUsername())){
            return ResponseEntity.badRequest().body("You can only edit your own posts.");
        }

        if (postCreateModel.body() == null || postCreateModel.body().length() > 200 || postCreateModel.body().length() < 10){
            return ResponseEntity.badRequest().body("Body must be 10-200 characters long");
        }
        postObj.setBody(postCreateModel.body());
        if (postCreateModel.title() == null || postCreateModel.title().length() > 100 || postCreateModel.title().length() < 5){
            return ResponseEntity.badRequest().body("Title must be between 5-100 characters");
        }
        postObj.setTitle(postCreateModel.title());
        postObj.setLastModifiedDate(new Date());

        postRepository.save(postObj);
        return ResponseEntity.ok(postObj);
    }

    public ResponseEntity<?> deletePost(Long id, HttpSession session){
        String loggedInUser = (String) session.getAttribute("loggedInUser");
        if (loggedInUser == null || userRepository.findById(loggedInUser).isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }
        if (postRepository.findById(id).isEmpty()){
            return ResponseEntity.badRequest().body("Invalid post");
        }
        Post post = postRepository.findById(id).get();
        Users user = userRepository.findById(loggedInUser).get();
        if (!post.getUsers().getUsername().equals(loggedInUser) && !user.getType().equals(UserType.ADMIN)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to delete this post");
        }
        List<PostInteractions> postInteractions = postInteractionRepository.findAllByPostID(id);

        for (PostInteractions currPostInteraction : postInteractions){
            postInteractionRepository.deleteById(currPostInteraction.getPostID()+"_"+currPostInteraction.getUsers());
        }

        postRepository.deleteById(id);
        return ResponseEntity.ok("Successfully deleted post!");
    }

}
