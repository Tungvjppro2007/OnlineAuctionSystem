package com.auction.common.model;

import java.time.LocalDateTime;

public class Art extends Item {
    private String artist;
    private int yearCreated;

    public Art(String id, LocalDateTime creatAt, String name, String description, double startingPrice, double currentPrice, String sellerId, String artist, int yearCreated) {
        super(id, creatAt, name, description, startingPrice, currentPrice, sellerId);
        this.artist = artist;
        this.yearCreated = yearCreated;
    }

    @Override
    public String getDetails() {
        return "Tác phẩm" + getName() + "| tác giả: "+artist+ " | năm sáng tác: " + yearCreated ;
    }
    public String getArtist() {
        return artist;
    }
    public int getYearCreated() {
        return yearCreated;
    }
    public void setArtist(String artist) {
        this.artist = artist;
    }
    public void setYearCreated(int yearCreated) {
        this.yearCreated = yearCreated;
    }
}