package com.videopostingsystem.videopostingsystem.users;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "users")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class Users implements Serializable {

    @Id
    private String username;
    @Column(unique = true)
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private UserType type;
    private String topCategory;
    private String country = "Unknown";
    private String bio = "I'm new here!";
    private Boolean enabled = false;
    private String cardColor = "white";
    private String backgroundColor = "whitesmoke";
    private String profilePic = "😀";

    public Users(String username, String email, String password, UserType type) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.type = type;
    }

    //TODO add follow feature
}
