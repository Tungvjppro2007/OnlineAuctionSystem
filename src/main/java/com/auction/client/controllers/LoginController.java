package com.auction.client.controllers;

import com.auction.client.util.SessionHolder;
import java.io.IOException;
import java.net.URL;
import javafx.concurrent.Task;
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
        String password = passwordField.getText(); // không trim mật khẩu

        // 1. Kiểm tra đầu vào rỗng
        if (username.isEmpty() || password.isEmpty()) {
            showError("⚠️ Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu!");
            return;
        }

        // 2. Xác thực ở thread nền để giao diện không bị đứng
        setBusy(true);
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                // TODO: thay bằng gửi yêu cầu LOGIN qua Socket sang Server
                return authenticate(username, password);
            }
        };
        task.setOnSucceeded(e -> {
            String role = task.getValue();
            SessionHolder.set(username, role);
            routeByRole(role);
        });
        task.setOnFailed(e -> {
            showError("❌ " + task.getException().getMessage());
            setBusy(false);
        });

        Thread thread = new Thread(task, "login-task");
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    public void handleGoToRegister() {
        navigateToScreen("/com/auction/views/register.fxml",
                "Đăng Ký Tài Khoản - Online Auction System", 980, 680);
    }

    /** Demo: trả về vai trò nếu đúng, sai thì ném lỗi. Sau này thay bằng Server. */
    private String authenticate(String username, String password) throws Exception {
        if (!"123456".equals(password)) {
            throw new Exception("Tên đăng nhập hoặc mật khẩu không chính xác!");
        }
        switch (username.toLowerCase()) {
            case "bidder":
                return "BIDDER";
            case "seller":
                return "SELLER";
            case "admin":
                return "ADMIN";
            default:
                throw new Exception("Tên đăng nhập hoặc mật khẩu không chính xác!");
        }
    }

    private void routeByRole(String role) {
        switch (role) {
            case "SELLER":
                navigateToScreen("/com/auction/views/seller_management.fxml",
                        "Quản Lý Đăng Bán - Online Auction System", 1120, 720);
                break;
            case "ADMIN": // TODO: đổi sang admin_dashboard.fxml khi có màn hình Admin
            default:
                navigateToScreen("/com/auction/views/auction_list.fxml",
                        "Danh Sách Phiên Đấu Giá - Online Auction System", 1100, 700);
        }
    }

    private void navigateToScreen(String fxmlPath, String title, int width, int height) {
        URL url = getClass().getResource(fxmlPath);
        if (url == null) {
            showError("⚠️ Không tìm thấy giao diện " + fxmlPath);
            setBusy(false);
            return;
        }
        try {
            Stage stage = (Stage) loginButton.getScene().getWindow();
            Parent root = FXMLLoader.load(url);
            stage.setScene(new Scene(root, width, height));
            stage.setTitle(title);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showError("⚠️ Không thể tải giao diện " + fxmlPath);
            setBusy(false);
        }
    }

    private void setBusy(boolean busy) {
        loginButton.setDisable(busy);
        usernameField.setDisable(busy);
        passwordField.setDisable(busy);
        loginButton.setText(busy ? "Đang đăng nhập..." : "Đăng nhập");
    }

    private void showError(String message) {
        statusLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
        statusLabel.setText(message);
    }

    private void clearStatus() {
        if (statusLabel != null) {
            statusLabel.setText("");
        }
    }
}