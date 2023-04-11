package com.videopostingsystem.videopostingsystem.posts;

import com.videopostingsystem.videopostingsystem.posts.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
