package com.auction.client.controllers;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

    @FXML
    private Button loginButton;

    @FXML
    public void initialize() {
        // Xóa thông báo lỗi khi người dùng bắt đầu gõ lại
        usernameField.textProperty().addListener((obs, oldText, newText) -> clearStatus());
        passwordField.textProperty().addListener((obs, oldText, newText) -> clearStatus());
    }

    @FXML
    public void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        // 1. Kiểm tra đầu vào rỗng
        if (username.isEmpty() || password.isEmpty()) {
            showError("⚠️ Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu!");
            return;
        }

        // 2. Logic kiểm tra tài khoản (Demo / Giả lập)
        // Sau này khi nối Socket, bạn sẽ gửi yêu cầu LOGIN sang Server tại đây
        if ("admin".equalsIgnoreCase(username) && "123456".equals(password)) {
            showSuccess("🎉 Đăng nhập thành công! Đang chuyển hướng...");
            navigateToScreen("/com/auction/views/auction_list.fxml", "Danh Sách Phiên Đấu Giá - Online Auction System", 1100, 700);
        } else if ("seller".equalsIgnoreCase(username) && "123456".equals(password)) {
            showSuccess("🎉 Đăng nhập thành công (Người bán)!");
            navigateToScreen("/com/auction/views/seller_management.fxml", "Quản Lý Đăng Bán - Online Auction System", 1120, 720);
        } else {
            showError("❌ Tên đăng nhập hoặc mật khẩu không chính xác!");
        }
    }

    @FXML
    public void handleGoToRegister() {
        navigateToScreen("/com/auction/views/register.fxml", "Đăng Ký Tài Khoản - Online Auction System", 980, 680);
    }

    private void navigateToScreen(String fxmlPath, String title, int width, int height) {
        try {
            Stage stage = (Stage) loginButton.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            stage.setScene(new Scene(root, width, height));
            stage.setTitle(title);
            stage.centerOnScreen();
        } catch (IOException e) {
            System.err.println("Không thể chuyển sang giao diện: " + fxmlPath);
            e.printStackTrace();
            showError("⚠️ Không thể tải giao diện " + fxmlPath);
        }
    }

    private void showError(String message) {statusLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
        statusLabel.setText(message);
    }

    private void showSuccess(String message) {
        statusLabel.setStyle("-fx-text-fill: #16a34a; -fx-font-weight: bold;");
        statusLabel.setText(message);
    }

    private void clearStatus() {
        if (statusLabel != null) {
            statusLabel.setText("");
        }
    }
}