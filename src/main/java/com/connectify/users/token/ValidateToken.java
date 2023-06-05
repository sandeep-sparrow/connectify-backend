package com.connectify.users.token;

import com.connectify.users.Users;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Getter
@Setter
public class ValidateToken {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String token;
    private Date createdAt;
    private Date expiresAt;
    @ManyToOne
    @JoinColumn(name = "users", nullable = false)
    private Users users;
    private String email;

    public ValidateToken(String token,
                         Users users,
                         String email) {
        this.token = token;
        this.createdAt = new Date();
        this.expiresAt = new Date(this.createdAt.getTime() + (1000 * 60 * 60 * 24 * 3));
        this.users = users;
        this.email = email;
    }

}
