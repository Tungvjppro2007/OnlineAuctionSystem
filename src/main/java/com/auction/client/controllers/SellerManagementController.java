package com.auction.client.controllers;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class SellerManagementController {

    @FXML private Label pageTitleLabel;

    @FXML void navToHome(ActionEvent event) { switchScene("/com/auction/views/auction_list.fxml", "Trang chủ"); }
    @FXML
    void navToCreateAuction(ActionEvent event) {
        switchScene("/com/auction/views/create_auction.fxml", "Đăng bài bán & Tạo phiên mới");
    }
    @FXML void navToWallet(ActionEvent event) { switchScene("/com/auction/views/user_profile.fxml", "Ví tiền"); }
    @FXML void navToProfile(ActionEvent event) { switchScene("/com/auction/views/user_profile.fxml", "Hồ sơ cá nhân"); }
    @FXML void handleLogout(ActionEvent event) { switchScene("/com/auction/views/login.fxml", "Đăng nhập hệ thống"); }

    private void switchScene(String fxmlPath, String title) {
        try {
            Stage stage = (Stage) pageTitleLabel.getScene().getWindow();
            boolean isMaximized = stage.isMaximized();

            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(title);

            stage.setMaximized(isMaximized);
            stage.show();
        } catch (IOException e) {
            System.err.println("Không thể chuyển màn hình: " + fxmlPath + " - " + e.getMessage());
            e.printStackTrace();
        }
    }
}