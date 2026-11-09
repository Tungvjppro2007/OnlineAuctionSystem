package com.auction.server.dao;

import com.auction.common.payload.BidTransaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BidTransactionDao {

    /**
     * Lưu 1 bid mới vào DB.
     * Trigger trg_bid_count sẽ tự động cập nhật auction_sessions.
     */
    public boolean save(BidTransaction bid) {
        String sql = """
            INSERT INTO bid_transactions (id, auction_id, bidder_id, amount, bid_time)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, java.util.UUID.fromString(bid.getBidId()));
            ps.setObject(2, java.util.UUID.fromString(bid.getAuctionId()));
            ps.setObject(3, java.util.UUID.fromString(bid.getBidderId()));
            ps.setDouble(4, bid.getAmount());
            ps.setTimestamp(5, Timestamp.valueOf(bid.getTimestamp()));

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Lỗi lưu bid: " + e.getMessage());
            return false;
        }
    }

    /**
     * Lấy lịch sử bid của 1 phiên (sắp xếp theo thời gian giảm dần)
     */
    public List<BidTransaction> findByAuctionId(String auctionId) {
        String sql = """
            SELECT * FROM bid_transactions
            WHERE auction_id = ?
            ORDER BY bid_time DESC
            """;

        List<BidTransaction> bids = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, java.util.UUID.fromString(auctionId));
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                bids.add(mapResultSetToBid(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi lấy lịch sử bid: " + e.getMessage());
        }
        return bids;
    }

    /**
     * Lấy tất cả bid của 1 user
     */
    public List<BidTransaction> findByBidderId(String bidderId) {
        String sql = """
            SELECT * FROM bid_transactions
            WHERE bidder_id = ?
            ORDER BY bid_time DESC
            """;

        List<BidTransaction> bids = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, java.util.UUID.fromString(bidderId));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                bids.add(mapResultSetToBid(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi lấy bid của user: " + e.getMessage());
        }
        return bids;
    }

    private BidTransaction mapResultSetToBid(ResultSet rs) throws SQLException {
        return new BidTransaction(
            rs.getString("id"),
            rs.getString("auction_id"),
            rs.getString("bidder_id"),
            rs.getDouble("amount"),
            rs.getTimestamp("bid_time").toLocalDateTime()
        );
    }
}