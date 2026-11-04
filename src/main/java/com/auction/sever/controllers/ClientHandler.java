package com.auction.sever.controllers;

import com.auction.common.payload.Message;
import com.auction.common.payload.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable{
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public ClientHandler(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            // Khởi tạo ObjectOutputStream trước ObjectInputStream để tránh deadlock
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            while (true) {
                // Đọc gói tin gửi từ client
                Message message = (Message) in.readObject();
                System.out.println("Nhận dữ liệu từ Client: " + message.getType());

                // Xử lý logic và phản hồi
                Message response = processMessage(message);
                sendMessage(response);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Client ngắt kết nối.");
        } finally {
            closeConnection();
        }
    }

    public void sendMessage(Message msg) {
        try {
            out.writeObject(msg);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Message processMessage(Message msg) {
        // Xử lý các loại yêu cầu (LOGIN, BID, v.v.)
        return new Message(MessageType.SUCCESS, "Đã xử lý xong");
    }

    private void closeConnection() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
