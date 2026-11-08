package com.auction.common.patterns;

import com.auction.common.payload.Message;

import java.util.Map;

public interface Subject {
    void registerObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers(Message message);
}
