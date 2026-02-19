package com.exam.model;

public class User {
    private String username;
    private String role;
    private String profilePicturePath;

    public User(String username, String role) {
        this.username = username;
        this.role = role;
        this.profilePicturePath = null;
    }

    public User(String username, String role, String profilePicturePath) {
        this.username = username;
        this.role = role;
        this.profilePicturePath = profilePicturePath;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public String getProfilePicturePath() {
        return profilePicturePath;
    }

    public void setProfilePicturePath(String profilePicturePath) {
        this.profilePicturePath = profilePicturePath;
    }
}