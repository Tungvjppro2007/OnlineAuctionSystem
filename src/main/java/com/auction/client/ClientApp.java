package com.auction.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // 1. Nạp giao diện FXML của màn hình Đăng nhập (login.fxml)
        Parent root = FXMLLoader.load(getClass().getResource("/com/auction/views/login.fxml"));
        
        // 2. Thiết lập tiêu đề cửa sổ ứng dụng
        primaryStage.setTitle("Hệ Thống Đấu Giá Trực Tuyến - VNU UET");
        
        // 3. Khởi tạo kích thước Scene ban đầu (Rộng x Cao: 950x650)
        Scene scene = new Scene(root, 950, 650);
      
        primaryStage.setScene(scene);
        
        // 4. Giới hạn kích thước nhỏ nhất để không bị vỡ giao diện khi co kéo
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);
        
        // 5. Căn giữa màn hình máy tính và bật cửa sổ lên
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public static void main(String[] args) {
        // Hàm main chính khởi động ứng dụng JavaFX
        launch(args);
    }
}