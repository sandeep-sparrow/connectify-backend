package com.videopostingsystem.videopostingsystem.posts;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "posts")
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

    private String users;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String body;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_date")
    private Date creationDate = new Date();

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_modified_date")
    private Date lastModifiedDate = new Date();

    @Column(nullable = true)
    private String tag;

    public Post(String users, String title, String body, String tag) {
        this.users = users;
        this.title = title;
        this.body = body;
        this.tag = tag;
    }

    public Post(){

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsers() {
        return users;
    }

    public void setUsers(String user) {
        this.users = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return Objects.equals(id, post.id) && Objects.equals(users, post.users) && Objects.equals(title, post.title) && Objects.equals(body, post.body) && Objects.equals(creationDate, post.creationDate) && Objects.equals(lastModifiedDate, post.lastModifiedDate) && Objects.equals(tag, post.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, users, title, body, creationDate, lastModifiedDate, tag);
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", user=" + users +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", creationDate=" + creationDate +
                ", lastModifiedDate=" + lastModifiedDate +
                ", tag='" + tag + '\'' +
                '}';
    }
}
