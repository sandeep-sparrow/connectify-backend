package com.videopostingsystem.videopostingsystem;

public class UserProfileModel {
    private String username;
    private String country;
    private String bio;
    private String topCategory;

    public UserProfileModel(String username, String country, String bio, String topCategory) {
        this.username = username;
        this.country = country;
        this.bio = bio;
        this.topCategory = topCategory;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getTopCategory() {
        return topCategory;
    }

    public void setTopCategory(String topCategory) {
        this.topCategory = topCategory;
    }
}
