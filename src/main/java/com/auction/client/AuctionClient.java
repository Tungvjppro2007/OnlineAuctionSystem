package com.auction.client;

import com.auction.common.model.user.Bidder;
import com.auction.common.model.user.UserRole;
import com.auction.common.payload.Message;
import com.auction.common.payload.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;

public class AuctionClient {
    private static final String SEVER_HOST ="localhost";
    private static final int SEVER_PORT = 8888;

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean isRunning = true;

    public void start(){
        try {
            System.out.println("🔌 Đang kết nối tới Server tại " + SEVER_HOST + ":" + SEVER_PORT + "...");
            socket = new Socket(SEVER_HOST,SEVER_PORT);
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
            System.out.println("Kết nối server thành công!");
            new Thread(this::listenForMessage).start();
            Bidder bidder = new Bidder("U001", LocalDateTime.now(),"nguyenvana","123456","Nguyen Van A","a@gmail.com", UserRole.BIDDER,1000.0);
            sendMessage(new Message(MessageType.BID,bidder));
            Thread.sleep(1000);
            sendMessage(new Message(MessageType.BID,150.0));
        }catch (IOException | InterruptedException e){
            System.err.println("Lỗi kết nối Client: "+e.getMessage());
        }
    }
    private void listenForMessage(){
        try{
            while (isRunning){
                Message msg = (Message) in.readObject();
                System.out.println("[Nhận từ server] loại: "+msg.getType()+" |Nội dung: "+msg.getData());
            }
        }catch (IOException | ClassNotFoundException e){
            System.out.println("Đã ngắt kết nối tới Server.");
        }finally {
            closeConnection();
        }
    }
    public void sendMessage(Message message){
        try{
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            System.err.println("Lỗi gửi dữ liệu:" + e.getMessage());
        }
    }
    private void closeConnection(){
        isRunning = false;
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public static void main(String[] args){
        new AuctionClient().start();
    }
}
