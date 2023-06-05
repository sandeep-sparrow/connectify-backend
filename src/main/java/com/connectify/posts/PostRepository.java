package com.connectify.posts;

import com.connectify.users.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByUsers(Users users);
    Page<Post> findAllByUsersOrderByLastModifiedDateDesc(Users users, Pageable pageable);
    Page<Post> findAll(Pageable pageable);

    Page<Post> findAllByCategoryAndUsersAndLastModifiedDateAfter(String category, Users users, Date lastModifiedDate, Pageable pageable);
    Page<Post> findAllByCategoryAndLastModifiedDateAfter(String category, Date lastModifiedDate, Pageable pageable);

    Page<Post> findAllByUsersAndLastModifiedDateAfter(Users users, Date lastModifiedDate, Pageable pageable);
    Page<Post> findAllByLastModifiedDateAfter(Date lastModifiedDate, Pageable pageable);

}
