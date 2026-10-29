package com.auction.common.model;

import java.time.LocalDateTime;

public class User extends Entity{
    private String username;
    private transient String password;
    private String fullName;
    private String email;
    private UserRole role;

    public User(String id, LocalDateTime createAt, String username, String password,String fullName,String email,UserRole role) {
        super(id, createAt);
        this.username=username;
        this.password=password;
        this.fullName=fullName;
        this.email=email;
        this.role=role;
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
    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public UserRole getRole() {
        return role;
    }
    public void setRole(UserRole role) {
        this.role = role;
    }
}
