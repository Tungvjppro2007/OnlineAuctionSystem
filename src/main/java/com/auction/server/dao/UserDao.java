package com.auction.server.dao;

import com.auction.common.model.user.*;
import com.auction.common.util.PasswordUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    // ============================================================
    // ĐĂNG KÝ TÀI KHOẢN
    // ============================================================

    /**
     * Đăng ký user mới (Bidder hoặc Seller).
     * Password được hash SHA-256 trước khi lưu.
     * @return true nếu đăng ký thành công
     */
    public boolean register(User user) {
        String sql = """
            INSERT INTO users (id, username, password_hash, full_name, email, role, balance, admin_level)
            VALUES (?, ?, ?, ?, ?, ?::user_role, ?, ?)
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, java.util.UUID.fromString(user.getId()));
            ps.setString(2, user.getUsername());
            ps.setString(3, PasswordUtil.hashPassword(user.getPassword()));
            ps.setString(4, user.getFullName());
            ps.setString(5, user.getEmail());
            ps.setString(6, user.getRole().name());

            // Bidder-specific: balance
            if (user instanceof Bidder bidder) {
                ps.setBigDecimal(7, java.math.BigDecimal.valueOf(bidder.getBalance()));
            } else {
                ps.setBigDecimal(7, java.math.BigDecimal.ZERO);
            }

            // Admin-specific: admin_level
            if (user instanceof Admin admin) {
                ps.setInt(8, admin.getAdminLevel());
            } else {
                ps.setNull(8, Types.SMALLINT);
            }

            int rows = ps.executeUpdate();

            // Nếu là Seller → insert thêm vào bảng sellers
            if (user instanceof Seller seller && rows > 0) {
                insertSeller(conn, seller);
            }

            return rows > 0;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi đăng ký user: " + e.getMessage());
            return false;
        }
    }

    /**
     * Insert thông tin seller mở rộng
     */
    private void insertSeller(Connection conn, Seller seller) throws SQLException {
        String sql = "INSERT INTO sellers (id, store_name, rating) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, java.util.UUID.fromString(seller.getId()));
            ps.setString(2, seller.getStoreName());
            ps.setDouble(3, seller.getRating());
            ps.executeUpdate();
        }
    }

    // ============================================================
    // ĐĂNG NHẬP
    // ============================================================

    /**
     * Xác thực đăng nhập bằng username + password.
     * @return User nếu đúng, null nếu sai
     */
    public User authenticate(String username, String plainPassword) {
        String sql = """
            SELECT u.*, s.store_name, s.rating
            FROM users u
            LEFT JOIN sellers s ON u.id = s.id
            WHERE u.username = ? AND u.is_active = TRUE
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                if (PasswordUtil.verifyPassword(plainPassword, storedHash)) {
                    return mapResultSetToUser(rs);
                }
            }
            return null; // Sai username hoặc password

        } catch (SQLException e) {
            System.err.println("❌ Lỗi đăng nhập: " + e.getMessage());
            return null;
        }
    }

    // ============================================================
    // TÌM KIẾM
    // ============================================================

    /**
     * Tìm user theo ID
     */
    public User findById(String userId) {
        String sql = """
            SELECT u.*, s.store_name, s.rating
            FROM users u
            LEFT JOIN sellers s ON u.id = s.id
            WHERE u.id = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, java.util.UUID.fromString(userId));
            ResultSet rs = ps.executeQuery();
            return rs.next() ? mapResultSetToUser(rs) : null;

        } catch (SQLException e) {
            System.err.println("❌ Lỗi tìm user: " + e.getMessage());
            return null;
        }
    }

    /**
     * Lấy danh sách tất cả users
     */
    public List<User> findAll() {
        String sql = """
            SELECT u.*, s.store_name, s.rating
            FROM users u
            LEFT JOIN sellers s ON u.id = s.id
            WHERE u.is_active = TRUE
            ORDER BY u.created_at DESC
            """;

        List<User> users = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi lấy danh sách user: " + e.getMessage());
        }
        return users;
    }

    // ============================================================
    // CẬP NHẬT
    // ============================================================

    /**
     * Cập nhật balance của Bidder (sau khi đặt giá hoặc nạp tiền)
     */
    public boolean updateBalance(String userId, double newBalance) {
        String sql = "UPDATE users SET balance = ? WHERE id = ? AND role = 'BIDDER'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBigDecimal(1, java.math.BigDecimal.valueOf(newBalance));
            ps.setObject(2, java.util.UUID.fromString(userId));
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Lỗi cập nhật balance: " + e.getMessage());
            return false;
        }
    }

    /**
     * Kiểm tra username đã tồn tại chưa
     */
    public boolean existsByUsername(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            return ps.executeQuery().next();

        } catch (SQLException e) {
            return false;
        }
    }

    // ============================================================
    // HELPER: Mapping ResultSet → Java Object
    // ============================================================

    /**
     * Chuyển 1 dòng ResultSet thành đúng loại User (Bidder / Seller / Admin)
     * dựa vào cột "role".
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
        String username = rs.getString("username");
        String fullName = rs.getString("full_name");
        String email = rs.getString("email");
        UserRole role = UserRole.valueOf(rs.getString("role"));

        switch (role) {
            case BIDDER -> {
                Bidder bidder = new Bidder(id, createdAt, username, null,
                    fullName, email, role, rs.getDouble("balance"));
                bidder.setId(id); // Đảm bảo dùng ID từ DB
                return bidder;
            }
            case SELLER -> {
                Seller seller = new Seller(id, createdAt, username, null,
                    fullName, email, role,
                    rs.getString("store_name"),
                    rs.getDouble("rating"));
                seller.setId(id);
                return seller;
            }
            case ADMIN -> {
                Admin admin = new Admin(id, createdAt, username, null,
                    fullName, email, role, rs.getInt("admin_level"));
                admin.setId(id);
                return admin;
            }
            default -> {
                User user = new User(id, createdAt, username, null,
                    fullName, email, role);
                user.setId(id);
                return user;
            }
        }
    }
}