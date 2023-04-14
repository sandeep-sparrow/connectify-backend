package com.videopostingsystem.videopostingsystem.posts.interaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostInteractionRepository extends JpaRepository<PostInteractions, String> {
    List<PostInteractions> findByUsers(String users);
    List<PostInteractions> findByPostID(Long postID);
}
