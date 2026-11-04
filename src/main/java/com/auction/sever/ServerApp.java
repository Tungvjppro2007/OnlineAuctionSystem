package com.auction.sever;

import java.net.ServerSocket;
import java.net.Socket;

public class ServerApp {
    private static final int PORT = 8080;

    public static void main(String[] args){
        try (ServerSocket serverSocket = new ServerSocket(PORT)){
            while(true){
                //Client connect
                Socket clientSocket = serverSocket.accept();
                System.out.println("New Client " + clientSocket.getInetAddress());

                C
            }
        }
    }
}
