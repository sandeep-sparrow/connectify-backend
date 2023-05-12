package com.connectify.users;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Users implements UserDetails {

    @Id
    private String username;
    @Column(unique = true)
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private UserType type;
    private String topCategory;
    private String country;
    private String bio;
    private Boolean enabled;
    private String cardColor;
    private String backgroundColor;
    private String profilePic;

    public Users(String username, String email, String password, UserType type) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.type = type;
        this.country = "Unknown";
        this.bio = "I'm new here!";
        this.topCategory = "blank";
        this.enabled = false;
        this.cardColor = "white";
        this.backgroundColor = "whitesmoke";
        this.profilePic = "😀";
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(type.name()));
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword(){
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
