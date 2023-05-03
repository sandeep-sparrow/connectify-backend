package com.videopostingsystem.videopostingsystem.posts.comments;

import com.videopostingsystem.videopostingsystem.posts.Post;
import com.videopostingsystem.videopostingsystem.users.Users;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import java.util.Date;

@Entity
@Table(name = "comments")
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_date")
    private Date creationDate = new Date();

    public Comment(Post post, Users user, String content) {
        this.post = post;
        this.user = user;
        this.content = content;
    }
}