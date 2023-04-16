package com.videopostingsystem.videopostingsystem.recommendSystem;

import com.videopostingsystem.videopostingsystem.posts.Post;
import com.videopostingsystem.videopostingsystem.posts.PostRepository;
import com.videopostingsystem.videopostingsystem.posts.interaction.PostInteractionRepository;
import com.videopostingsystem.videopostingsystem.posts.interaction.PostInteractions;
import com.videopostingsystem.videopostingsystem.users.UserRepository;
import jakarta.servlet.http.HttpSession;
import java.util.*;

public class UserLikeFrequencies {

    public static String mostLikedCategory(HttpSession session, UserRepository userRepository, PostRepository postRepository, PostInteractionRepository postInteractionRepository) {
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
