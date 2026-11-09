package com.auction.server.dao;

import com.auction.common.model.item.*;
import com.auction.common.patterns.ItemFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ItemDao {

    /**
     * Lưu item mới vào DB. Dùng category để xác định cột nào cần lưu.
     */
    public boolean save(Item item, ItemCategory category) {
        String sql = """
            INSERT INTO items (id, name, description, starting_price, current_price,
                               seller_id, category,
                               brand, warranty_months,
                               artist, year_created,
                               model_year, mileage)
            VALUES (?, ?, ?, ?, ?, ?, ?::item_category, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, java.util.UUID.fromString(item.getId()));
            ps.setString(2, item.getName());
            ps.setString(3, item.getDescription());
            ps.setDouble(4, item.getStartingPrice());
            ps.setDouble(5, item.getCurrentPrice());
            ps.setObject(6, java.util.UUID.fromString(item.getSellerId()));
            ps.setString(7, category.name());

            // Category-specific columns — set null cho các cột không thuộc category
            if (item instanceof Electronics e) {
                ps.setString(8, e.getBrand());
                ps.setInt(9, e.getWarrantyMonths());
            } else {
                ps.setNull(8, Types.VARCHAR);
                ps.setNull(9, Types.SMALLINT);
            }

            if (item instanceof Art a) {
                ps.setString(10, a.getArtist());
                ps.setInt(11, a.getYearCreated());
            } else {
                ps.setNull(10, Types.VARCHAR);
                ps.setNull(11, Types.SMALLINT);
            }

            if (item instanceof Vehicle v) {
                ps.setInt(12, v.getModelYear());
                ps.setDouble(13, v.getMileage());
            } else {
                ps.setNull(12, Types.SMALLINT);
                ps.setNull(13, Types.DECIMAL);
            }

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Lỗi lưu item: " + e.getMessage());
            return false;
        }
    }

    /**
     * Tìm item theo ID. Dùng ItemFactory để tạo đúng loại subclass.
     */
    public Item findById(String itemId) {
        String sql = "SELECT * FROM items WHERE id = ? AND is_active = TRUE";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, java.util.UUID.fromString(itemId));
            ResultSet rs = ps.executeQuery();
            return rs.next() ? mapResultSetToItem(rs) : null;

        } catch (SQLException e) {
            System.err.println("❌ Lỗi tìm item: " + e.getMessage());
            return null;
        }
    }

    /**
     * Lấy tất cả items của 1 seller
     */
    public List<Item> findBySellerId(String sellerId) {
        String sql = "SELECT * FROM items WHERE seller_id = ? AND is_active = TRUE ORDER BY created_at DESC";
        return queryItems(sql, sellerId);
    }

    /**
     * Lấy tất cả items theo category
     */
    public List<Item> findByCategory(ItemCategory category) {
        String sql = "SELECT * FROM items WHERE category = ?::item_category AND is_active = TRUE";
        return queryItems(sql, category.name());
    }

    /**
     * Lấy tất cả items đang active
     */
    public List<Item> findAll() {
        String sql = "SELECT * FROM items WHERE is_active = TRUE ORDER BY created_at DESC";
        List<Item> items = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                items.add(mapResultSetToItem(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi lấy danh sách item: " + e.getMessage());
        }
        return items;
    }

    // ============================================================
    // HELPERS
    // ============================================================

    private List<Item> queryItems(String sql, String param) {
        List<Item> items = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, param.length() == 36
                ? java.util.UUID.fromString(param) : param);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                items.add(mapResultSetToItem(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi query items: " + e.getMessage());
        }
        return items;
    }

    /**
     * Dùng ItemFactory để tạo đúng subclass dựa trên cột category
     */
    private Item mapResultSetToItem(ResultSet rs) throws SQLException {
        ItemCategory category = ItemCategory.valueOf(rs.getString("category"));
        String id = rs.getString("id");
        LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
        String name = rs.getString("name");
        String description = rs.getString("description");
        double startingPrice = rs.getDouble("starting_price");
        double currentPrice = rs.getDouble("current_price");
        String sellerId = rs.getString("seller_id");

        // Dùng ItemFactory (design pattern đã có sẵn trong dự án)
        Object[] extraArgs = switch (category) {
            case ELECTRONICS -> new Object[]{
                rs.getString("brand"),
                rs.getInt("warranty_months")
            };
            case ART -> new Object[]{
                rs.getString("artist"),
                rs.getInt("year_created")
            };
            case VEHICLE -> new Object[]{
                rs.getInt("model_year"),
                rs.getDouble("mileage")
            };
        };

        Item item = ItemFactory.createItem(category, id, createdAt,
            name, description, startingPrice, currentPrice, sellerId, extraArgs);
        item.setId(id); // Đảm bảo giữ ID từ DB
        return item;
    }
}