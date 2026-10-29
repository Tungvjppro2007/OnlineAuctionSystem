package com.auction.common.model;

import java.time.LocalDateTime;

public class Admin extends User{
    private int adminLevel;
    public Admin(String id, LocalDateTime createAt, String username, String password, String fullName, String email, UserRole role, int adminLevel) {
        super(id, createAt, username, password, fullName, email, role);
        this.adminLevel = adminLevel;
    }
    public int getAdminLevel() {
        return adminLevel;
    }
    public void setAdminLevel(int adminLevel) {
        this.adminLevel = adminLevel;
    }
}
