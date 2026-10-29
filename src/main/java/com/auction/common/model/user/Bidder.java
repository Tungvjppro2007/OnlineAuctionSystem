package com.auction.common.model;

import java.time.LocalDateTime;

public class Bidder extends User{
    private double balance;
    public Bidder(String id, LocalDateTime createAt, String username, String password, String fullName, String email, UserRole role,double balance) {
        super(id, createAt, username, password, fullName, email, role);
        this.balance=balance;
    }
    public void deposit(double amount){
        if (amount > 0){
            balance+=amount;
        }else{
            throw new IllegalArgumentException();
        }
    }
    //@throws
    public void withdraw(double amount){
        if (amount>0 && amount <= balance){
            balance-=amount;
        }else {
            throw new IllegalArgumentException();
        }
    }
}
