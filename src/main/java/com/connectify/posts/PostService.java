package com.connectify.posts;

import com.connectify.openapi.OpenAPI;
import com.connectify.posts.comments.Comment;
import com.connectify.posts.comments.CommentRepository;
import com.connectify.posts.interaction.PostInteractionRepository;
import com.connectify.posts.interaction.PostInteractions;
import com.connectify.users.UserRepository;
import com.connectify.users.UserType;
import com.connectify.users.Users;
import com.connectify.users.config.JwtService;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
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
    private final CommentRepository commentRepository;
    private final JwtService jwtService;

    public ResponseEntity<?> createPost(PostCreateModel post, HttpServletRequest request){
        Gson gson = new Gson();
        PostStatus status;
        String json;
        String username = jwtService.getUsername(request);
        if (post.title() == null || post.title().length() < 5 || post.title().length() > 50){
            status = new PostStatus("invalid", "Title must be longer than 5 characters and less than 50 characters");
            json = gson.toJson(status);
            return ResponseEntity.badRequest().body(json);
        }
        if (post.body() == null || post.body().length() > 500 || post.body().length() < 10) {
            status = new PostStatus("invalid", "Body must be between 10-500 characters");
            json = gson.toJson(status);
            return ResponseEntity.badRequest().body(json);
        }
        Users user = userRepository.getReferenceById(username);
        Post newPost = new Post(user, post.title(), post.body());
        String category = OpenAPI.request("categorize this content in 1 of these 20 categories returning only the ONE WORD of the category. " +
                "However, if you view the content as very offensive return the word 'Invalid'. Only return 'Invalid' on posts very offensive or racist. Do not categorize as 'Invalid' if it is just risque.:" +
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
                "History\n" +
                "Animals\n" +
                "Miscellaneous\n" +
                "Cars\n" +
                "Philosophy\n" +
                "Photography\n" +
                "Movies\n" +
                "Home and Garden\n" +
                "Career\n" +
                "Relationships\n" +
                "Society\n" +
                "Parenting\n" +
                "Space\n" +
                "DIY\n" +
                "Cooking\n" +
                "Adventure\n" +
                "Spirituality\n" +
                "Fitness\n" +
                "Real Estate\n" +
                "Psychology\n" +
                "Personal Finance\n" +
                "Hobbies\n" +
                "Here is the content:" +
                post.title() + post.body());
        category = category.toLowerCase();
        if (category.contains("invalid")){
            status = new PostStatus("invalid", "This content does not adhere to our post policies.");
            json = gson.toJson(status);
            return ResponseEntity.badRequest().body(json);
        }
        newPost.setCategory(category);
        postRepository.save(newPost);
        status = new PostStatus("valid", "successfully posted!");
        json = gson.toJson(status);
        return ResponseEntity.ok(json);
    }

    public ResponseEntity<?> getPosts(int page){
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "lastModifiedDate"));
        List<Post> posts = postRepository.findAll(pageRequest).getContent();
        List<PostResponseModel> modelPosts = new ArrayList<>();
        for (Post post : posts){
            List<PostInteractions> likedList = postInteractionRepository.findAllLikedByPostID(post.getId());
            List<PostInteractions> bookmarkedList = postInteractionRepository.findAllBookmarkedByPostID(post.getId());
            modelPosts.add(new PostResponseModel(post.getId(),
                    post.getUsers().getUsername(),
                    post.getTitle(), post.getBody(),
                    (long) likedList.size(), (long) bookmarkedList.size(),
                    post.getLastModifiedDate(),
                    post.getCategory()));
        }
        return ResponseEntity.ok(modelPosts);
    }

    public ResponseEntity<?> getPostsByCategoryAndDateAndUser(int page, String category, String user, int lastDays){
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "lastModifiedDate"));
        LocalDate localDate = LocalDate.now().minusDays(lastDays);
        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        List<Post> posts;

        Optional<Users> userOptional = userRepository.findById(user);



        if ((category != null && !category.equals(""))){
            posts = userOptional.map(users -> postRepository.findAllByCategoryAndUsersAndLastModifiedDateAfter(category, users, date, pageRequest).getContent())
                    .orElseGet(() -> postRepository.findAllByCategoryAndLastModifiedDateAfter(category, date, pageRequest).getContent());

        } else {
            posts = userOptional.map(users -> postRepository.findAllByUsersAndLastModifiedDateAfter(users, date, pageRequest).getContent())
                    .orElseGet(() -> postRepository.findAllByLastModifiedDateAfter(date, pageRequest).getContent());
        }

        List<PostResponseModel> modelPosts = new ArrayList<>();
        for (Post post : posts){
            List<PostInteractions> likedList = postInteractionRepository.findAllLikedByPostID(post.getId());
            List<PostInteractions> bookmarkedList = postInteractionRepository.findAllBookmarkedByPostID(post.getId());
            modelPosts.add(new PostResponseModel(post.getId(),
                    post.getUsers().getUsername(),
                    post.getTitle(), post.getBody(),
                    (long) likedList.size(), (long) bookmarkedList.size(),
                    post.getLastModifiedDate(),
                    post.getCategory()));
        }
        return ResponseEntity.ok(modelPosts);
    }

    public ResponseEntity<?> getPost(Long id){

        if (postRepository.findById(id).isEmpty()) {
            return ResponseEntity.badRequest().body("Post ID not valid.");
        }
        Post post = postRepository.findById(id).get();
        List<PostInteractions> likedList = postInteractionRepository.findAllLikedByPostID(post.getId());
        List<PostInteractions> bookmarkedList = postInteractionRepository.findAllBookmarkedByPostID(post.getId());
        return ResponseEntity.ok(new PostResponseModel(post.getId(),
                post.getUsers().getUsername(),
                post.getTitle(), post.getBody(),
                (long) likedList.size(), (long) bookmarkedList.size(),
                post.getLastModifiedDate(),
                post.getCategory()));
    }

    public ResponseEntity<?> updatePost(Long id, PostCreateModel postCreateModel, HttpServletRequest request){
        String username = jwtService.getUsername(request);

        Optional<Post> post = postRepository.findById(id);
        if (post.isEmpty()){
            return ResponseEntity.badRequest().body("Post ID not valid.");
        }
        Post postObj = post.get();
        if (!username.equals(postObj.getUsers().getUsername())){
            return ResponseEntity.badRequest().body("You can only edit your own posts.");
        }

        if (postCreateModel.body() == null || postCreateModel.body().length() > 200 || postCreateModel.body().length() < 10){
            return ResponseEntity.badRequest().body("Body must be 10-200 characters long");
        }
        if (postCreateModel.title() == null || postCreateModel.title().length() > 100 || postCreateModel.title().length() < 5){
            return ResponseEntity.badRequest().body("Title must be between 5-100 characters");
        }

        String category = OpenAPI.request("categorize this content in 1 of these 20 categories returning only the ONE WORD of the category. " +
                "However, if you view the content as very offensive return the word 'Invalid'. Only return 'Invalid' on posts very offensive or racist. Do not categorize as 'Invalid' if it is just risque.:" +
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
                "History\n" +
                "Animals\n" +
                "Miscellaneous\n" +
                "Cars\n" +
                "Philosophy\n" +
                "Photography\n" +
                "Movies\n" +
                "Home and Garden\n" +
                "Career\n" +
                "Relationships\n" +
                "Society\n" +
                "Parenting\n" +
                "Space\n" +
                "DIY\n" +
                "Cooking\n" +
                "Adventure\n" +
                "Spirituality\n" +
                "Fitness\n" +
                "Real Estate\n" +
                "Psychology\n" +
                "Personal Finance\n" +
                "Hobbies\n" +
                "Here is the content:" +
                postCreateModel.title() + postCreateModel.body());
        category = category.toLowerCase();
        if (category.contains("invalid")){
            PostStatus status = new PostStatus("invalid", "This content does not adhere to our post policies.");
            Gson gson = new Gson();
            String json = gson.toJson(status);
            return ResponseEntity.badRequest().body(json);
        }

        postObj.setBody(postCreateModel.body());
        postObj.setTitle(postCreateModel.title());
        postObj.setCategory(category);
        postObj.setLastModifiedDate(new Date());

        postRepository.save(postObj);
        return ResponseEntity.ok(postObj);
    }

    public ResponseEntity<?> deletePost(Long id, HttpServletRequest request){
        String username = jwtService.getUsername(request);
        if (postRepository.findById(id).isEmpty()){
            return ResponseEntity.badRequest().body("Invalid post");
        }
        Post post = postRepository.findById(id).get();
        Users user = userRepository.findById(username).get();
        if (!post.getUsers().getUsername().equals(username) && !user.getType().equals(UserType.ADMIN)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to delete this post");
        }
        List<PostInteractions> postInteractions = postInteractionRepository.findAllByPostID(id);

        for (PostInteractions currPostInteraction : postInteractions){
            postInteractionRepository.deleteById(currPostInteraction.getPostID()+"_"+currPostInteraction.getUsers());
        }

        List<Comment> comments = commentRepository.findAllByPost(post);

        for (Comment comment : comments){
            commentRepository.deleteById(comment.getId());
        }

        postRepository.deleteById(id);
        return ResponseEntity.ok("Successfully deleted post!");
    }

    public ResponseEntity<?> getUserPosts(String user, int page) {
        if (user == null || userRepository.findById(user).isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user");
        }
        Users userObj = userRepository.findById(user).get();
        return postGetter(userObj, page);
    }

    public ResponseEntity<?> myPosts(int page, HttpServletRequest request) {
        String username = jwtService.getUsername(request);
        Users user = userRepository.findById(username).get();
        return postGetter(user, page);
    }

    public ResponseEntity<?> postGetter(Users user, int page){
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "lastModifiedDate"));
        List<Post> posts = postRepository.findAllByUsersOrderByLastModifiedDateDesc(user, pageRequest).getContent();
        List<PostResponseModel> modelPosts = new ArrayList<>();
        for (Post post : posts){
            List<PostInteractions> likedList = postInteractionRepository.findAllLikedByPostID(post.getId());
            List<PostInteractions> bookmarkedList = postInteractionRepository.findAllBookmarkedByPostID(post.getId());
            modelPosts.add(new PostResponseModel(post.getId(),
                    post.getUsers().getUsername(),
                    post.getTitle(), post.getBody(),
                    (long) likedList.size(), (long) bookmarkedList.size(),
                    post.getLastModifiedDate(),
                    post.getCategory()));
        }
        return ResponseEntity.ok(modelPosts);
    }
}
