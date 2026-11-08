package com.auction.server.controllers;

import com.auction.common.exceptions.*;
import com.auction.common.model.AuctionSession;
import com.auction.common.model.AuctionStatus;
import com.auction.common.model.user.Bidder;

import java.time.LocalDateTime;

public class AuctionTest {
    public static void main(String[] args) {
        AuctionManager manager = new AuctionManager();

        AuctionSession session = new AuctionSession();
        session.setAuctionId("AUC101");
        session.setCurrentPrice(100.0);
        session.setStatus(AuctionStatus.RUNNING);
        session.setEndTime(LocalDateTime.now().plusMinutes(30));

        manager.addSession(session);

        Bidder bidderA = new Bidder();
        bidderA.setId("USER_A");

        Bidder bidderB = new Bidder();
        bidderB.setId("USER_B");

        System.out.println("================ THỬ NGHIỆM KỊCH BẢN ================\n");

        // TEST 1
        System.out.println("[TEST 1] User A đặt giá $150:");
        try {
            manager.processBid("AUC101", bidderA.getId(), 150.0);
            System.out.println("-> Thành công! Giá hiện tại: $" + manager.getSession("AUC101").getCurrentPrice());
        } catch (Exception e) {
            System.out.println("-> Thất bại: " + e.getMessage());
        }

        // TEST 2
        System.out.println("\n[TEST 2] User B đặt giá $120 (thấp hơn hiện tại):");
        try {
            manager.processBid("AUC101", bidderB.getId(), 120.0);
            System.out.println("-> Thành công!");
        } catch (InvalidBidException e) {
            System.out.println("-> Bắt Exception thành công: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("-> Lỗi không xác định: " + e.getMessage());
        }

        // TEST 3
        System.out.println("\n[TEST 3] User A đặt giá vào mã phiên sai (AUC999):");
        try {
            manager.processBid("AUC999", bidderA.getId(), 200.0);
        } catch (InvalidBidException e) {
            System.out.println("-> Bắt Exception thành công: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("-> Lỗi không xác định: " + e.getMessage());
        }

        // TEST 4
        System.out.println("\n[TEST 4] Đặt giá khi phiên đã đóng (FINISHED):");
        session.setStatus(AuctionStatus.FINISHED);
        try {
            manager.processBid("AUC101", bidderB.getId(), 200.0);
        } catch (AuctionClosedException e) {
            System.out.println("-> Bắt Exception thành công: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("-> Lỗi không xác định: " + e.getMessage());
        }
    }
}