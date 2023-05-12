package com.connectify.posts.interaction;

import com.connectify.users.Users;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "post_interactions")
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class PostInteractions {

    @Id
    private String id;
    Long postID;
    @ManyToOne
    @JoinColumn(name = "users", nullable = false)
    Users users;
    private boolean liked;
    private boolean bookmark;

    public PostInteractions(Long postID, Users users, boolean liked, boolean bookmark){
        this.postID = postID;
        this.users = users;
        this.liked = liked;
        this.bookmark = bookmark;
        this.id = postID + "_" + users.getUsername();
    }

}
