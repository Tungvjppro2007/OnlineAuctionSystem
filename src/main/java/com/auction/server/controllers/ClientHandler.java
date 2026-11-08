package com.auction.server.controllers;

import com.auction.common.model.*;
import com.auction.common.model.item.Item;
import com.auction.common.model.user.User;
import com.auction.common.patterns.Observer;
import com.auction.common.payload.BidTransaction;
import com.auction.common.payload.Message;
import com.auction.common.payload.MessageType;
import com.auction.common.exceptions.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable, Observer {
    private Socket socket;
    private AuctionManager auctionManager;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean isConnected = true;

    public ClientHandler(Socket socket,AuctionManager auctionManager) {
        this.socket = socket;
        this.auctionManager=auctionManager;
    }
    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());

            while (isConnected && !socket.isClosed()) {
                Object receivedObj = in.readObject();
                if (receivedObj instanceof Message msg) {
                    handlerMessage(msg);
                }
            }
        } catch (Exception e) {
            // 🚀 In vết lỗi ra Console Server để dễ chẩn đoán
            System.err.println("⚠️ Ngắt kết nối Client (" + socket.getInetAddress() + "): " + e.getMessage());
        } finally {
            closeConnection();
        }
    }
    private void handlerMessage(Message msg) throws IOException {
        switch (msg.getType()) {
            case LOGIN -> {
                if (msg.getData() instanceof User user) {
                    handleLogin(user);
                }
            }
            case CREAT_ITEM -> {
                if (msg.getData() instanceof Item item) {
                    handleCreateItem(item);
                }
            }
            case BID -> {
                if (msg.getData() instanceof BidTransaction bidTx) {
                    handleBid(bidTx);
                }
            }
            default -> sendMessage(new Message(MessageType.RESPONSE_ERROR, "Yêu cầu không hợp lệ!"));
        }
    }

    /**
     * Xử lý yêu cầu đăng nhập
     */
    private void handleLogin(User user) {
        System.out.println("🔑 Client đăng nhập: " + user.getUsername());
        sendMessage(new Message(MessageType.RESPONSE_SUCCESS, "Đăng nhập thành công!"));
    }

    /**
     * Xử lý yêu cầu tạo sản phẩm đấu giá mới
     */
    private void handleCreateItem(Item item) {
        System.out.println("📦 Tạo sản phẩm mới: " + item.getName());
        sendMessage(new Message(MessageType.RESPONSE_SUCCESS, "Tạo sản phẩm thành công!"));
    }

    /**
     * Xử lý nghiệp vụ Đặt giá (BID) & Đăng ký Observer
     */
    private void handleBid(BidTransaction bidTx) {
        AuctionSession session = auctionManager.getSession(bidTx.getAuctionId());

        // Kiểm tra an toàn tránh NullPointerException
        if (session == null) {
            sendMessage(new Message(MessageType.RESPONSE_ERROR, "Không tìm thấy phiên đấu giá: " + bidTx.getAuctionId()));
            return;
        }

        // 1. Đăng ký ClientHandler này làm Observer để nhận tin nhắn Broadcast của phiên
        session.registerObserver(this);

        try {
            // 2. Thực thi xử lý đặt giá qua AuctionManager
            auctionManager.processBid(
                    bidTx.getAuctionId(),
                    bidTx.getBidderId(),
                    bidTx.getAmount()
            );

            // 3. Phản hồi kết quả đặt giá thành công cho Client vừa đặt
            sendMessage(new Message(MessageType.RESPONSE_SUCCESS, "Đặt giá thành công!"));

        } catch (InvalidBidException | AuctionClosedException e) {
            // 4. Bắt ngoại lệ và gửi thông báo lỗi tương ứng về cho Client
            sendMessage(new Message(MessageType.RESPONSE_ERROR, e.getMessage()));
        }
    }
    public void sendMessage(Message msg){
        try{
            out.writeObject(msg);
            out.flush();
        }catch (IOException e){
            System.err.println("Lỗi dữ liệu gửi tới Client: "+e.getMessage());
        }
    }
    private void closeConnection(){
        try{
            if (in != null) in.close();
            if (out!=null) out.close();
            if (socket!=null) socket.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void update(Message message) {
        try{
            if (out != null){
                out.writeObject(message);
                out.flush();
            }
        }catch (Exception e){
            System.err.println("❌ Lỗi khi gửi broadcast tới Client: " + e.getMessage());
        }
    }
}
