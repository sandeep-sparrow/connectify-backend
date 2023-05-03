package com.videopostingsystem.videopostingsystem.posts.comments;

import com.videopostingsystem.videopostingsystem.posts.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPost(Post post);
}