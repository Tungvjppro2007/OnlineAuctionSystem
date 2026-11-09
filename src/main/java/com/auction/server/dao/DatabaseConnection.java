package com.auction.server.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton quản lý kết nối PostgreSQL.
 * Sử dụng: Connection conn = DatabaseConnection.getConnection();
 */
public class DatabaseConnection {

    private static final String URL = "jdbc:postgresql://127.0.0.1:5432/online_auction_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "your_password"; // ← đổi password tại đây

    // Đảm bảo driver được load (Java 21 thường tự detect, nhưng để chắc chắn)
    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Không tìm thấy PostgreSQL Driver!", e);
        }
    }

    /**
     * Lấy connection mới. Caller có trách nhiệm đóng connection sau khi dùng.
     * Nên dùng try-with-resources.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Test kết nối
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("❌ Kết nối DB thất bại: " + e.getMessage());
            return false;
        }
    }
}