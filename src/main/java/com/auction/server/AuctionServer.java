package com.auction.server;

import com.auction.common.payload.Message;
import com.auction.server.controllers.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class AuctionServer {
    private static final int PORT = 8888;
    private static final List<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private boolean isRunning = true;
    public void start(){
        System.out.println("🚀 Server Đấu Giá đang khởi động tại port " + PORT + "...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("✅ Server đã sẵn sàng lắng nghe kết nối từ Client!");
            AuctionManager auctionManager= new AuctionManager();
            while (isRunning){
                Socket clientSocket = serverSocket.accept();
                System.out.println("🔌 Client mới kết nối từ IP: " + clientSocket.getInetAddress().getHostAddress());
                ClientHandler clientHandler = new ClientHandler(clientSocket,auctionManager);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        }catch (IOException e){
            System.err.println("❌ Lỗi Server: " + e.getMessage());
        }
    }
    private void broadcast(Message message){
        for (ClientHandler client: clients){
            client.sendMessage(message);
        }

    }
    public static void removeClient(ClientHandler client){
        clients.remove(client);
        System.out.println("🔌 Đã dọn dẹp Client. Số kết nối còn lại: " + clients.size());
    }
    public static void main(String[] args){
        new AuctionServer().start();
    }
}
