package com.videopostingsystem.videopostingsystem.users;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "users")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Users implements Serializable {

    @Id
    private String username;
    @Column(unique = true)
    private String email;
    private String password;
    private String type;
    private String topCategory;
    private Boolean enabled = false;

    public Users() {
    }
    public Users(String username, String email, String password, String type) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public String getTopCategory() {
        return topCategory;
    }

    public void setTopCategory(String topCategory) {
        this.topCategory = topCategory;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
