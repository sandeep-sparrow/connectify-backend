package com.connectify.recommendSystem;

import com.connectify.posts.Post;
import com.connectify.posts.PostRepository;
import com.connectify.posts.interaction.PostInteractions;
import com.connectify.users.UserRepository;
import com.connectify.posts.interaction.PostInteractionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class FeedService {

    public static String[] likedCategories(String username, UserRepository userRepository, PostRepository postRepository, PostInteractionRepository postInteractionRepository) {

        List<PostInteractions> postInteractionsList = postInteractionRepository.findAllByUsers(userRepository.findById(username).get());
        Map<String, Integer> categoryCountMap = new HashMap<>();
        for (PostInteractions postInteractions : postInteractionsList) {
            if (postRepository.findById(postInteractions.getPostID()).isPresent()){
                Post post = postRepository.findById(postInteractions.getPostID()).get();
                categoryCountMap.put(post.getCategory(), categoryCountMap.getOrDefault(post.getCategory(), 0) + 1);
            }
        }
        List<Map.Entry<String, Integer>> sortedCategoryEntries = new ArrayList<>(categoryCountMap.entrySet());
        sortedCategoryEntries.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        return sortedCategoryEntries.stream().map(Map.Entry::getKey).toArray(String[]::new);
    }

}
