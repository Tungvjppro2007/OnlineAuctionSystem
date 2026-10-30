package com.auction.common.model;

import com.auction.common.payload.BidTransaction;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AuctionSession implements Serializable {
    private static final long serialVersionUID = 1l;
    private String auctionId;
    private Item item;
    private double currentPrice;
    private Bidder highestBidder;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AuctionStatus status;
    private List<BidTransaction> bidHistory;

    public AuctionSession(String auctionId, Item item, double currentPrice, LocalDateTime startTime, LocalDateTime endTime, AuctionStatus status, List<BidTransaction> bidHistory) {
        this.auctionId = auctionId;
        this.item = item;
        this.currentPrice = item.getStartingPrice();
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = AuctionStatus.OPEN;
        this.bidHistory = new ArrayList<>();
    }
    public synchronized boolean placeBid(Bidder bidder,double amount){
        if(status != AuctionStatus.RUNNING){
            return false;
        }
        if(LocalDateTime.now().isAfter(endTime)){
            this.status = AuctionStatus.FINISHED;
            return false;
        }
        if (amount<=currentPrice){
            return false;
        }
        this.currentPrice = amount;
        this.highestBidder=bidder;

        BidTransaction tx = new BidTransaction(
                "BID-" + System.currentTimeMillis(),
                this.auctionId,
                bidder.getId(),
                amount,
                LocalDateTime.now()
        );

        this.bidHistory.add(tx);

        return true;
    }
    public String getAuctionId() { return auctionId; }
    public Item getItem() { return item; }
    public double getCurrentPrice() { return currentPrice; }
    public Bidder getHighestBidder() { return highestBidder; }
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
}
