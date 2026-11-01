package com.auction.client.controllers;

import java.io.File;
import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class CreateAuctionController {

    @FXML private Label formTitleLabel;
    @FXML private TextField itemNameField;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private TextField startPriceField;
    @FXML private TextField stepPriceField;
    @FXML private TextField durationField;
    @FXML private TextArea descriptionArea;
    @FXML private Label imagePathLabel;
    @FXML private ImageView previewImageView;
    @FXML private Label messageLabel;

    private File selectedImageFile;

    @FXML
    public void initialize() {
        if (categoryComboBox != null) {
            categoryComboBox.setItems(FXCollections.observableArrayList(
                "Thiết bị điện tử", "Xe cộ", "Tranh nghệ thuật", "Trang sức", "Khác"
            ));
        }
    }

    @FXML
    public void handleSelectImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn ảnh sản phẩm");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Hình ảnh", "*.png", "*.jpg", "*.jpeg")
        );

        Stage stage = (Stage) formTitleLabel.getScene().getWindow();
        selectedImageFile = fileChooser.showOpenDialog(stage);

        if (selectedImageFile != null) {
            imagePathLabel.setText(selectedImageFile.getName());
            previewImageView.setImage(new Image(selectedImageFile.toURI().toString()));
        }
    }

    @FXML
    public void handleSubmitAuction() {
        String name = itemNameField.getText().trim();
        String startPriceStr = startPriceField.getText().trim();
        String category = categoryComboBox.getValue();

        if (name.isEmpty() || startPriceStr.isEmpty() || category == null) {
            messageLabel.setStyle("-fx-text-fill: #ef4444;");
            messageLabel.setText("⚠️ Vui lòng điền đầy đủ các thông tin bắt buộc!");
            return;
        }

        try {
            double startPrice = Double.parseDouble(startPriceStr);
            
            messageLabel.setStyle("-fx-text-fill: #10b981;");
            messageLabel.setText("✅ Đăng bài bán & tạo phiên đấu giá thành công!");
            
            // Chuyển về trang danh sách sản phẩm
            switchScene("/com/auction/views/auction_list.fxml", "UET AUCTION - Trang chủ");
        } catch (NumberFormatException e) {
            messageLabel.setStyle("-fx-text-fill: #ef4444;");
            messageLabel.setText("⚠️ Giá nhập phải ở định dạng số!");
        }
    }

    @FXML
    public void handleCancel() {
        switchScene("/com/auction/views/auction_list.fxml", "UET AUCTION - Trang chủ");
    }

    // --- CÁC HÀM NAVBAR DÙNG CHUNG ---
    @FXML void navToHome(ActionEvent event) { switchScene("/com/auction/views/auction_list.fxml", "Trang chủ"); }
    @FXML void navToMyItems(ActionEvent event) { switchScene("/com/auction/views/seller_management.fxml", "Vật phẩm của tôi"); }
    @FXML void navToWallet(ActionEvent event) { switchScene("/com/auction/views/user_profile.fxml", "Ví tiền"); }
    @FXML void navToProfile(ActionEvent event) { switchScene("/com/auction/views/user_profile.fxml", "Hồ sơ cá nhân"); }
    @FXML void handleLogout(ActionEvent event) { switchScene("/com/auction/views/login.fxml", "Đăng nhập hệ thống"); }

    private void switchScene(String fxmlPath, String title) {
        try {
            Stage stage = (Stage) formTitleLabel.getScene().getWindow();
            boolean isMaximized = stage.isMaximized();

            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(title);

            stage.setMaximized(isMaximized);
            stage.show();
        } catch (IOException e) {
            System.err.println("Chưa thể nạp màn hình: " + fxmlPath + " - " + e.getMessage());
            e.printStackTrace();
        }
    }
}