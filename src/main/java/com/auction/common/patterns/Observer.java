package main.java.com.auction.common.patterns;

import model.Auction;

public interface Observer {
    /**
     * Được gọi khi trạng thái của Auction thay đổi (giá mới, người dẫn đầu mới, kết thúc phiên,...)
     * @param auction Phiên đấu giá phát ra sự kiện
     */
    void update(Auction auction);
}