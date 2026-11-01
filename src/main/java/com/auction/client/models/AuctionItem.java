package com.auction.client.models;

public class AuctionItem {
    private String id;
    private String name;
    private String category;
    private double currentPrice;
    private String highestBidder;
    private String timeLeft;
    private String imagePath;
    private String status;

    // Constructor đầy đủ tham số
    public AuctionItem(String id, String name, String category, double currentPrice, String highestBidder, String timeLeft, String imagePath, String status) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.currentPrice = currentPrice;
        this.highestBidder = highestBidder;
        this.timeLeft = timeLeft;
        this.imagePath = imagePath;
        this.status = status;
    }

    // Constructor rút gọn (dùng ảnh mặc định nếu chưa có)
    public AuctionItem(String id, String name, String category, double currentPrice, String highestBidder, String timeLeft) {
        this(id, name, category, currentPrice, highestBidder, timeLeft, "/com/auction/images/uet_logo.png", "Đang diễn ra");
    }

    // --- GETTERS & SETTERS ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(double currentPrice) { this.currentPrice = currentPrice; }

    public String getHighestBidder() { return highestBidder; }
    public void setHighestBidder(String highestBidder) { this.highestBidder = highestBidder; }

    public String getTimeLeft() { return timeLeft; }
    public void setTimeLeft(String timeLeft) { this.timeLeft = timeLeft; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}