package com.connectify.posts;

import com.connectify.users.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByUsers(Users users);
    Page<Post> findAllByUsersOrderByLastModifiedDateDesc(Users users, Pageable pageable);
    Page<Post> findAll(Pageable pageable);
}
