package com.auction.common.model;

import java.time.LocalDateTime;

public class Electronics extends Item {
    private String brand;
    private int warrantyMonths;

    public Electronics(String id, LocalDateTime creatAt, String name, String description, double startingPrice, double currentPrice, String sellerId, String brand, int warrantyMonths) {
        super(id, creatAt, name, description, startingPrice, currentPrice, sellerId);
        this.brand = brand;
        this.warrantyMonths = warrantyMonths;
    }

    @Override
    public String getDetails() {
        return "Điện tử: " + getName() + " | Thương hiệu: "+ brand + " | Bảo hành: "+warrantyMonths+" tháng";
    }
    public String getBrand() {
        return brand;
    }
    public int getWarrantyMonths() {
        return warrantyMonths;
    }
    public void setBrand(String brand) {
        this.brand = brand;
    }
    public void setWarrantyMonths(int warrantyMonths) {
        this.warrantyMonths = warrantyMonths;
    }
}