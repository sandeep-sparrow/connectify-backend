package com.connectify.posts;

import com.connectify.users.Users;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import java.util.Date;

@Entity
@Table(name = "posts")
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class Post {

    @Id
    @SequenceGenerator(
            name = "post_id_sequence",
            sequenceName = "post_id_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "post_id_sequence"
    )
    private Long id;

    @ManyToOne
    @JoinColumn(name = "users", nullable = false)
    private Users users;

    @Column(
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String title;

    @Column(
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String body;

    private Long likes = 0L;

    private Long bookmarks = 0L;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_date")
    private Date creationDate = new Date();

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_modified_date")
    private Date lastModifiedDate = new Date();

    @Column(nullable = false)
    private String category;


    public Post(Users users, String title, String body) {
        this.users = users;
        this.title = title;
        this.body = body;
    }
}
