package com.auction.server.services;

import com.auction.server.dao.DatabaseConnection;

import java.sql.*;
import java.util.UUID;

public class PaymentService {

    /**
     * Tạo thanh toán cho phiên đấu giá đã kết thúc.
     * @param auctionId ID phiên đấu giá
     * @param payerId   ID người thắng (highest bidder)
     * @param sellerId  ID người bán
     * @param amount    Số tiền thanh toán (giá cuối cùng)
     * @param method    Phương thức: BANK_TRANSFER, CREDIT_CARD, E_WALLET
     * @return paymentId nếu thành công, null nếu thất bại
     */
    public String createPayment(String auctionId, String payerId,
                                String sellerId, double amount, String method) {
        String sql = """
            INSERT INTO payments (id, auction_id, payer_id, seller_id, amount, method, status)
            VALUES (?, ?, ?, ?, ?, ?::payment_method, 'PENDING'::payment_status)
            """;

        String paymentId = UUID.randomUUID().toString();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, UUID.fromString(paymentId));
            ps.setObject(2, UUID.fromString(auctionId));
            ps.setObject(3, UUID.fromString(payerId));
            ps.setObject(4, UUID.fromString(sellerId));
            ps.setDouble(5, amount);
            ps.setString(6, method);

            if (ps.executeUpdate() > 0) {
                return paymentId;
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi tạo payment: " + e.getMessage());
        }
        return null;
    }

    /**
     * Xác nhận thanh toán thành công
     */
    public boolean confirmPayment(String paymentId, String transactionRef) {
        String sql = """
            UPDATE payments
            SET status = 'COMPLETED'::payment_status,
                transaction_ref = ?,
                paid_at = NOW()
            WHERE id = ? AND status = 'PENDING'::payment_status
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, transactionRef);
            ps.setObject(2, UUID.fromString(paymentId));

            if (ps.executeUpdate() > 0) {
                // Cập nhật trạng thái phiên đấu giá thành PAID
                updateAuctionStatusToPaid(conn, paymentId);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi xác nhận payment: " + e.getMessage());
        }
        return false;
    }

    private void updateAuctionStatusToPaid(Connection conn, String paymentId)
            throws SQLException {
        String sql = """
            UPDATE auction_sessions
            SET status = 'PAID'::auction_status
            WHERE id = (SELECT auction_id FROM payments WHERE id = ?)
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, UUID.fromString(paymentId));
            ps.executeUpdate();
        }
    }
}