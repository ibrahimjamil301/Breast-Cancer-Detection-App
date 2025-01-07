package com.example.Breast_Cancer;
public class User {
    private String fullName;
    private String email;
    private String UserId;

    public User() {

    }
    public User(String fullName, String email, String UserId) {
        this.fullName = fullName;
        this.email = email;
        this.UserId = UserId;
    }
    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }
    public String getUserId() {
        return  UserId;
    }
    public void setUserId(String UserId) {
        this.UserId = UserId;
    }
}
