package com.auction.server.controllers;

import com.auction.common.model.AuctionSession;
import com.auction.common.model.AuctionStatus;
import com.auction.common.model.item.Electronics;
import com.auction.common.model.item.Item;
import com.auction.common.exceptions.*;


import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AuctionManager {
    private final Map<String, AuctionSession> sessions = new ConcurrentHashMap<>();

    public void addSession(AuctionSession session){
        if (session != null){
            sessions.put(session.getAuctionId(),session);
        }
    }
    public AuctionSession getSession(String auctionId){
        return sessions.get(auctionId);
    }
    public synchronized void processBid(String auctionId, String bidder,double amount)
        throws AuctionClosedException, InvalidBidException {
        AuctionSession session = sessions.get(auctionId);
        if (session == null) {
            throw new InvalidBidException("Phiên đấu giá" + auctionId + " không tồn tại");
        }
        if (session.getStatus() != AuctionStatus.RUNNING
                || LocalDateTime.now().isAfter(session.getEndTime())) {
            session.setStatus(AuctionStatus.FINISHED);
            throw new AuctionClosedException("Phiên đấu giá " + auctionId + " đã kết thúc");
        }

        if (amount <= session.getCurrentPrice()){
            throw new InvalidBidException("Giá đặt ($" + amount
                    + ") phải lớn hơn giá hiện tại ($" + session.getCurrentPrice() + ").");

        }
        session.placeBid(bidder,amount);
    }
    public AuctionManager() {
        // 🚀 1. Khởi tạo đối tượng Electronics với đầy đủ 9 tham số theo đúng thứ tự
        Item item = new Electronics(
                "ITM01",                        // String id
                LocalDateTime.now(),            // LocalDateTime creatAt
                "Laptop Dell Vostro",           // String name
                "Laptop core i7",               // String description
                100.0,                          // double startingPrice
                100.0,                          // double currentPrice
                "SELLER_01",                    // String sellerId (phải để trong ngoặc kép "")
                "Dell",                         // String brand
                24                              // int warrantyMonths
        );

        // 🚀 2. Khởi tạo phiên AUC101
        AuctionSession session1 = new AuctionSession(
                "AUC101",                       // auctionId
                item,                           // item
                100.0,                          // currentPrice
                LocalDateTime.now(),            // startTime
                LocalDateTime.now().plusDays(1),// endTime
                AuctionStatus.RUNNING,           // status (hoặc AuctionStatus.OPEN tùy enum của bạn)
                new java.util.ArrayList<>()     // bidHistory
        );

        // 🚀 3. Lưu phiên vào danh sách
        sessions.put(session1.getAuctionId(), session1);
    }


}
