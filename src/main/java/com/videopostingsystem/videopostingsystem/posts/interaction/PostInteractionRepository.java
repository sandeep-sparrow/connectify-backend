package com.videopostingsystem.videopostingsystem.posts.interaction;

import com.videopostingsystem.videopostingsystem.posts.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostInteractionRepository extends JpaRepository<PostInteractions, String> {
    List<PostInteractions> findByUsers(String users);
}
