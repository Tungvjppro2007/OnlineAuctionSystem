package com.auction.common.model;

import java.time.LocalDateTime;

public class Vehicle extends Item {
    private int modelYear;
    private double mileage;

    public Vehicle(String id, LocalDateTime creatAt, String name, String description, double startingPrice, double currentPrice, String sellerId, int modelYear, double mileage) {
        super(id, creatAt, name, description, startingPrice, currentPrice, sellerId);
        this.modelYear = modelYear;
        this.mileage = mileage;
    }

    @Override
    public String getDetails() {
        return "Phương tiện: "+getName()+" | Năm sản xuất: "+modelYear+" | số km đã đi: "+mileage +" km";
    }
    public int getModelYear() {
        return modelYear;
    }
    public double getMileage() {
        return mileage;
    }
    public void setModelYear(int modelYear) {
        this.modelYear = modelYear;
    }
    public void setMileage(double mileage) {
        this.mileage = mileage;
    }
}

