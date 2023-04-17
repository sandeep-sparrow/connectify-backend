package com.videopostingsystem.videopostingsystem.posts;

import com.videopostingsystem.videopostingsystem.users.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByUsers(Users users);
}
