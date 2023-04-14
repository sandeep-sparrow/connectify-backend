package com.videopostingsystem.videopostingsystem.recommendSystem;

import com.videopostingsystem.videopostingsystem.posts.Post;
import com.videopostingsystem.videopostingsystem.posts.PostRepository;
import com.videopostingsystem.videopostingsystem.posts.interaction.PostInteractionRepository;
import com.videopostingsystem.videopostingsystem.posts.interaction.PostInteractions;
import com.videopostingsystem.videopostingsystem.users.UserRepository;
import com.videopostingsystem.videopostingsystem.users.Users;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class UserLikeFrequencies {

    public static String mostLikedCategory(HttpSession session, UserRepository userRepository, PostRepository postRepository, PostInteractionRepository postInteractionRepository) {
        String user = ((String) session.getAttribute("loggedInUser"));
        if (user == null || userRepository.findById(user).isEmpty()) {
            return null;
        }

        List<PostInteractions> postInteractionsList = postInteractionRepository.findByUsers(user);
        Map<String, Integer> categoryCountMap = new HashMap<>();
        for (int i = 0; i < postInteractionsList.size(); i++) {
            Post post = postRepository.findById(postInteractionsList.get(i).getPostID()).get();
            categoryCountMap.put(post.getCategory(), categoryCountMap.getOrDefault(post.getCategory(), 0) + 1);
        }
        String mostLikedCategory = "";
        int maxCount = 0;
        for (Map.Entry<String, Integer> entry : categoryCountMap.entrySet()) {
            if (entry.getValue() > maxCount) {
                mostLikedCategory = entry.getKey();
                maxCount = entry.getValue();
            }
        }
        return mostLikedCategory;
    }

}
