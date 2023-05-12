package com.connectify.users.follow;

import com.connectify.users.Users;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"follower", "following"})})
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "follower", nullable = false)
    private Users follower;

    @ManyToOne
    @JoinColumn(name = "following", nullable = false)
    private Users following;

    public Follow(Users follower, Users following){
        this.follower = follower;
        this.following = following;
    }

}
