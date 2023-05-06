package com.videopostingsystem.videopostingsystem.posts.interaction;

import com.videopostingsystem.videopostingsystem.users.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostInteractionRepository extends JpaRepository<PostInteractions, String> {
    List<PostInteractions> findAllByUsers(Users users);
    List<PostInteractions> findAllByPostID(Long postID);
    void deleteAllByPostID(Long postID);
    void deleteAllByUsers(Users users);

    @Query("SELECT p FROM PostInteractions p WHERE p.postID = :postID AND p.liked = true")
    List<PostInteractions> findAllLikedByPostID(@Param("postID") Long postID);
}
