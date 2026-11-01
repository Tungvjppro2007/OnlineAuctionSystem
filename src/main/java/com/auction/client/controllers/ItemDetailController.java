package com.auction.client.controllers;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class ItemDetailController {

    @FXML private ImageView itemImageView;
    @FXML private Label itemNameLabel;
    @FXML private Label itemIdLabel;
    @FXML private Label categoryLabel;
    @FXML private Label statusLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label currentPriceLabel;
    @FXML private Label highestBidderLabel;
    @FXML private Label stepPriceLabel;
    @FXML private Label timeLeftLabel;
    @FXML private TextField bidAmountField;
    @FXML private Label bidMessageLabel;

    private String currentItemId;

    // Hàm nhận dữ liệu từ AuctionListController truyền sang
    public void setItemData(String id, String name, String category, String price) {
        this.currentItemId = id;
        if (itemIdLabel != null) itemIdLabel.setText("Mã phiên: #" + id);
        if (itemNameLabel != null) itemNameLabel.setText(name);
        if (categoryLabel != null) categoryLabel.setText(category.toUpperCase());
        if (currentPriceLabel != null) currentPriceLabel.setText(price + " VNĐ");
        
        // Thử nạp ảnh đại diện nếu có
        try {
            Image img = new Image(getClass().getResourceAsStream("/com/auction/images/uet_logo.png"));
            if (itemImageView != null) itemImageView.setImage(img);
        } catch (Exception e) {
            // Dùng ảnh mặc định khi không tìm thấy tệp
        }
    }

    @FXML
    public void handlePlaceBid() {
        if (bidAmountField == null || bidMessageLabel == null) return;
        
        String inputAmount = bidAmountField.getText().trim();
        if (inputAmount.isEmpty()) {
            bidMessageLabel.setText("⚠️ Vui lòng nhập số tiền hợp lệ!");
            return;
        }

        try {
            double amount = Double.parseDouble(inputAmount);
            bidMessageLabel.setStyle("-fx-text-fill: #10b981;");
            bidMessageLabel.setText("✅ Đã đặt giá thành công: " + String.format("%,.0f", amount) + " VNĐ");
            bidAmountField.clear();
        } catch (NumberFormatException e) {
            bidMessageLabel.setStyle("-fx-text-fill: #ef4444;");
            bidMessageLabel.setText("⚠️ Số tiền phải là định dạng số nguyên!");
        }
    }

    @FXML
    public void handleJoinLiveRoom(ActionEvent event) {
        switchScene("/com/auction/views/live_bidding.fxml", "Sàn Đấu Giá Realtime");
    }

    @FXML
    public void handleBack(ActionEvent event) {
        switchScene("/com/auction/views/auction_list.fxml", "UET AUCTION - Trang chủ");
    }

    // Các hàm chuyển đổi trang trên Navbar (Khớp đúng 2 tham số với switchScene)
    @FXML void navToHome(ActionEvent event) { switchScene("/com/auction/views/auction_list.fxml", "Trang chủ"); }
    @FXML void navToMyItems(ActionEvent event) { switchScene("/com/auction/views/seller_management.fxml", "Quản lý vật phẩm"); }
    @FXML
    void navToCreateAuction(ActionEvent event) {
        switchScene("/com/auction/views/create_auction.fxml", "Đăng bài bán & Tạo phiên mới");
    }
    @FXML void navToWallet(ActionEvent event) { switchScene("/com/auction/views/user_profile.fxml", "Ví tiền"); }
    @FXML void navToProfile(ActionEvent event) { switchScene("/com/auction/views/user_profile.fxml", "Hồ sơ cá nhân"); }
    @FXML void handleLogout(ActionEvent event) { switchScene("/com/auction/views/login.fxml", "Đăng nhập hệ thống"); }

    // Hàm chuyển cảnh dùng chung - Giữ nguyên trạng thái Maximize màn hình
    private void switchScene(String fxmlPath, String title) {
        try {
            // Lấy Stage từ itemNameLabel thay cho searchField
            Stage stage = (Stage) itemNameLabel.getScene().getWindow();
            
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