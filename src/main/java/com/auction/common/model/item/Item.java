package com.auction.common.model;

import java.time.LocalDateTime;

public abstract class Item extends Entity{
    private String name;
    private String description;
    private double startingPrice;
    private double currentPrice;
    private String sellerId;

    public Item(String id, LocalDateTime creatAt, String name, String description, double startingPrice, double currentPrice, String sellerId) {
        super(id, creatAt);
        this.name = name;
        this.description = description;
        this.startingPrice = startingPrice;
        this.currentPrice = currentPrice;
        this.sellerId = sellerId;
    }

    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public double getStartingPrice() {
        return startingPrice;
    }
    public double getCurrentPrice() {
        return currentPrice;
    }
    public String getSellerId() {
        return sellerId;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setStartingPrice(double startingPrice) {
        this.startingPrice = startingPrice;
    }
    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }
    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }
    public abstract String getDetails();
}





