package com.connectify.posts;

import com.connectify.users.Users;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByUsers(Users users);
    List<Post> findAllByOrderByLikesDesc(Pageable pageable);
    List<Post> findByCategoryOrderByLikesDesc(String category, Pageable pageable);
}
