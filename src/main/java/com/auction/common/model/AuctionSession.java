package com.auction.common.model;

import com.auction.common.model.item.Item;
import com.auction.common.patterns.Observer;
import com.auction.common.patterns.Subject;
import com.auction.common.payload.BidTransaction;
import com.auction.common.payload.Message;
import com.auction.common.payload.MessageType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class AuctionSession implements Serializable, Subject {
    private static final long serialVersionUID = 1l;
    private String auctionId;
    private Item item;
    private double currentPrice;
    private String highestBidder;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AuctionStatus status;
    private List<BidTransaction> bidHistory;
    private final transient List<Observer> observers = new CopyOnWriteArrayList<>();
    
    public AuctionSession(String auctionId, Item item, LocalDateTime startTime, LocalDateTime endTime) {
        this.auctionId = auctionId;
        this.item = item;
        this.currentPrice = item.getStartingPrice();
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = AuctionStatus.OPEN;
        this.bidHistory = new ArrayList<>();
    }
    // Constructor LOAD TỪ DB (AuctionDao cần cái này)
    public AuctionSession(String auctionId, Item item, double currentPrice, String highestBidder, LocalDateTime startTime, LocalDateTime endTime, AuctionStatus status, List<BidTransaction> bidHistory) {
        this.auctionId = auctionId;
        this.item = item;
        this.currentPrice = currentPrice;
        this.highestBidder = highestBidder;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.bidHistory = (bidHistory != null) ? bidHistory : new ArrayList<>();
    }
    public AuctionSession(){}
    public synchronized boolean placeBid(String bidder, double amount) {
        if (amount <= currentPrice) {
            return false;
        }

        this.currentPrice = amount;
        this.highestBidder = bidder;

        // 1. Tạo giao dịch và lưu vào lịch sử đấu giá
        BidTransaction tx = new BidTransaction(
                "BID-" + System.currentTimeMillis(),
                this.auctionId,
                bidder,
                amount,
                LocalDateTime.now()
        );
        this.bidHistory.add(tx);

        // 2. Tự động phát thông báo giá mới tới tất cả Client đang xem
        String broadcastContent = String.format("📢 UPDATE: Phiên %s vừa có giá mới: $%.2f bởi %s",
                this.auctionId, amount, bidder);
        notifyObservers(new Message(MessageType.NOTIFICATION, broadcastContent));

        return true;
    }
    public String getAuctionId() { return auctionId; }
    public Item getItem() { return item; }
    public double getCurrentPrice() { return currentPrice; }
    public String getHighestBidder() { return highestBidder; }
    public AuctionStatus getStatus() { return status; }
    public void setStatus(AuctionStatus status) { this.status = status; }
    public List<BidTransaction> getBidHistory() { return bidHistory; }
    public LocalDateTime getStartTime() {
        return startTime;
    }
    public LocalDateTime getEndTime() {
        return endTime;
    }
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    public void setAuctionId(String auctionId) {
        this.auctionId = auctionId;
    }
    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public boolean isClosed() {
        return status == AuctionStatus.FINISHED || LocalDateTime.now().isAfter(endTime);
    }
    @Override
    public void registerObserver(Observer observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(Message message) {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }
}
