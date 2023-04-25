package com.videopostingsystem.videopostingsystem.recommendSystem;

import com.sun.tools.jconsole.JConsoleContext;
import com.videopostingsystem.videopostingsystem.posts.Post;
import com.videopostingsystem.videopostingsystem.posts.PostRepository;
import com.videopostingsystem.videopostingsystem.posts.PostResponseModel;
import com.videopostingsystem.videopostingsystem.posts.interaction.PostInteractionRepository;
import com.videopostingsystem.videopostingsystem.posts.interaction.PostInteractions;
import com.videopostingsystem.videopostingsystem.users.UserRepository;
import com.videopostingsystem.videopostingsystem.users.Users;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class FeedService {

    UserRepository userRepository;
    PostRepository postRepository;
    PostInteractionRepository postInteractionRepository;


    public ResponseEntity<?> feed(HttpSession session){
        int responseSize = 10;
        String username = (String) session.getAttribute("loggedInUser");
        if (username == null || userRepository.findById(username).isEmpty()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not logged in.");
        }
        Users user = userRepository.findById(username).get();
        if (user.getTopCategory().equalsIgnoreCase("blank") || postRepository.findAllByUsers(user).isEmpty()){
            System.out.println("testtttttt");
            Pageable pageable = PageRequest.of(0, 10);
            List<Post> mostLikedPosts = postRepository.findAllByOrderByLikesDesc(pageable);
            List<PostResponseModel> posts = new ArrayList<>();
            for (Post post : mostLikedPosts) {
                posts.add(new PostResponseModel(post.getId(), post.getUsers().getUsername(), post.getTitle(), post.getBody(), post.getLikes(), post.getBookmarks(), post.getLastModifiedDate(), post.getCategory()));
            }
            return ResponseEntity.ok(posts);
        }

        String[] topCategories = likedCategories(session, userRepository, postRepository, postInteractionRepository);
        assert topCategories != null;
        float[] frequencies = frequency(topCategories.length);

        List<Post> feed = new ArrayList<>();
        for (int i = 0; i < frequencies.length; i++) {
            Pageable pageable = PageRequest.of(0, (int) (frequencies[i]*responseSize));
            feed.addAll(postRepository.findByCategoryOrderByLikesDesc(topCategories[i], pageable));
        }
        List<PostResponseModel> posts = new ArrayList<>();
        for (Post post : feed){
            posts.add(new PostResponseModel(post.getId(), post.getUsers().getUsername(), post.getTitle(), post.getBody(), post.getLikes(), post.getBookmarks(), post.getLastModifiedDate(), post.getCategory()));
        }

        return ResponseEntity.ok(posts);
    }


    public float[] frequency(int likedCategoriesLength){
        if (likedCategoriesLength ==  0){
            return new float[]{0};
        }if (likedCategoriesLength == 1){
            return new float[]{1};
        }
        if (likedCategoriesLength == 2){
            return new float[]{0.8f, 0.2f};
        }
        return new float[]{0.6f, 0.2f, 0.2f};
    }
    public static String[] likedCategories(HttpSession session, UserRepository userRepository, PostRepository postRepository, PostInteractionRepository postInteractionRepository) {
        String user = ((String) session.getAttribute("loggedInUser"));
        if (user == null || userRepository.findById(user).isEmpty()) {
            return null;
        }

        List<PostInteractions> postInteractionsList = postInteractionRepository.findAllByUsers(userRepository.findById(user).get());
        Map<String, Integer> categoryCountMap = new HashMap<>();
        for (PostInteractions postInteractions : postInteractionsList) {
            Post post = postRepository.findById(postInteractions.getPostID()).get();
            categoryCountMap.put(post.getCategory(), categoryCountMap.getOrDefault(post.getCategory(), 0) + 1);
        }
        List<Map.Entry<String, Integer>> sortedCategoryEntries = new ArrayList<>(categoryCountMap.entrySet());
        sortedCategoryEntries.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        return sortedCategoryEntries.stream().map(Map.Entry::getKey).toArray(String[]::new);
    }

}
