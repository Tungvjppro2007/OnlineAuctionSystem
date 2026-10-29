package com.auction.common.model;

import java.time.LocalDateTime;

public class Seller extends User{
    private String storeName;
    private double rating;

    public Seller(String id, LocalDateTime createAt, String username, String password, String fullName, String email, UserRole role, String storeName, double rating) {
        super(id, createAt, username, password, fullName, email, role);
        this.storeName = storeName;
        this.rating = rating;
    }
    public String getStoreName() {
        return storeName;
    }
    public double getRating() {
        return rating;
    }
    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }
    public void setRating(double rating) {
        this.rating = rating;
    }
}
