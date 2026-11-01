package com.auction.client.util;

public final class SessionHolder {
    private static String username;
    private static String role; // BIDDER | SELLER | ADMIN

    private SessionHolder() { }

    public static void set(String user, String userRole) {
        username = user;
        role = userRole;
    }
    public static String getUsername() { return username; }
    public static String getRole() { return role; }
    public static void clear() { username = null; role = null; }
}