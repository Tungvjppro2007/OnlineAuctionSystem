package com.auction.server.dao;

import com.auction.common.model.AuctionSession;
import com.auction.common.model.AuctionStatus;
import com.auction.common.model.item.Item;
import com.auction.common.payload.BidTransaction;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AuctionDao {

    private final ItemDao itemDao = new ItemDao();
    private final BidTransactionDao bidDao = new BidTransactionDao();

    /**
     * Tạo phiên đấu giá mới
     */
    public boolean create(AuctionSession session) {
        String sql = """
            INSERT INTO auction_sessions (id, item_id, current_price, start_time, end_time, status)
            VALUES (?, ?, ?, ?, ?, ?::auction_status)
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, java.util.UUID.fromString(session.getAuctionId()));
            ps.setObject(2, java.util.UUID.fromString(session.getItem().getId()));
            ps.setDouble(3, session.getCurrentPrice());
            ps.setTimestamp(4, Timestamp.valueOf(session.getStartTime()));
            ps.setTimestamp(5, Timestamp.valueOf(session.getEndTime()));
            ps.setString(6, session.getStatus().name());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Lỗi tạo phiên đấu giá: " + e.getMessage());
            return false;
        }
    }

    /**
     * Tìm phiên đấu giá theo ID (kèm Item và lịch sử bid)
     */
    public AuctionSession findById(String auctionId) {
        String sql = "SELECT * FROM auction_sessions WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, java.util.UUID.fromString(auctionId));
            ResultSet rs = ps.executeQuery();
            return rs.next() ? mapResultSetToSession(rs) : null;

        } catch (SQLException e) {
            System.err.println("❌ Lỗi tìm phiên: " + e.getMessage());
            return null;
        }
    }

    /**
     * Lấy danh sách phiên đang chạy (OPEN hoặc RUNNING)
     */
    public List<AuctionSession> findActiveSessions() {
        String sql = """
            SELECT * FROM auction_sessions
            WHERE status IN ('OPEN', 'RUNNING')
              AND end_time > NOW()
            ORDER BY end_time ASC
            """;

        return querySessions(sql);
    }

    /**
     * Lấy tất cả phiên đấu giá
     */
    public List<AuctionSession> findAll() {
        String sql = "SELECT * FROM auction_sessions ORDER BY created_at DESC";
        return querySessions(sql);
    }

    /**
     * Cập nhật trạng thái phiên
     */
    public boolean updateStatus(String auctionId, AuctionStatus status) {
        String sql = "UPDATE auction_sessions SET status = ?::auction_status WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status.name());
            ps.setObject(2, java.util.UUID.fromString(auctionId));
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Lỗi cập nhật trạng thái: " + e.getMessage());
            return false;
        }
    }

    /**
     * Tự động đóng các phiên đã hết giờ
     * Nên gọi định kỳ (ví dụ: mỗi 30 giây)
     */
    public int closeExpiredSessions() {
        String sql = """
            UPDATE auction_sessions
            SET status = 'FINISHED'::auction_status
            WHERE status IN ('OPEN', 'RUNNING')
              AND end_time <= NOW()
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("❌ Lỗi đóng phiên hết giờ: " + e.getMessage());
            return 0;
        }
    }

    // ============================================================
    // HELPERS
    // ============================================================

    private List<AuctionSession> querySessions(String sql) {
        List<AuctionSession> sessions = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                sessions.add(mapResultSetToSession(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi query phiên: " + e.getMessage());
        }
        return sessions;
    }

    private AuctionSession mapResultSetToSession(ResultSet rs) throws SQLException {
        String auctionId = rs.getString("id");
        String itemId = rs.getString("item_id");
        double currentPrice = rs.getDouble("current_price");
        String highestBidderId = rs.getString("highest_bidder_id");
        LocalDateTime startTime = rs.getTimestamp("start_time").toLocalDateTime();
        LocalDateTime endTime = rs.getTimestamp("end_time").toLocalDateTime();
        AuctionStatus status = AuctionStatus.valueOf(rs.getString("status"));

        // Load item từ DB
        Item item = itemDao.findById(itemId);

        // Load lịch sử bid
        List<BidTransaction> bidHistory = bidDao.findByAuctionId(auctionId);

        // Dùng constructor "load từ DB"
        return new AuctionSession(auctionId, item, currentPrice,
            highestBidderId, startTime, endTime, status, bidHistory);
    }
}