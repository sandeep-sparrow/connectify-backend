package com.videopostingsystem.videopostingsystem.posts.interaction;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "PostInteractions")
public class PostInteractions {

    @Id
    private String id;
    Long postID;
    String users;
    private boolean liked;
    private boolean bookmark;

    public PostInteractions(Long postID, String users, boolean liked, boolean bookmark){
        this.postID = postID;
        this.users = users;
        this.liked = liked;
        this.bookmark = bookmark;
        this.id = postID + "_" + users;
    }

    public PostInteractions() {

    }

    public Long getPostID() {
        return postID;
    }

    public void setPostID(Long postID) {
        this.postID = postID;
    }

    public String getUsers() {
        return users;
    }

    public void setUsers(String user) {
        this.users = user;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean like) {
        this.liked = like;
    }

    public boolean isBookmark() {
        return bookmark;
    }

    public void setBookmark(boolean bookmark) {
        this.bookmark = bookmark;
    }


}
