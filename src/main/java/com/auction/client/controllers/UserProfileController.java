package com.auction.client.controllers;

import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class UserProfileController {

    // --- MÀN HÌNH CHÍNH & TAB PANES ---
    @FXML private Pane paneProfile;
    @FXML private Pane paneWallet;
    @FXML private Pane paneSettings;

    // --- CÁC NÚT TAB SIDE-MENU ---
    @FXML private Button btnTabProfile;
    @FXML private Button btnTabWallet;
    @FXML private Button btnTabSettings;

    // --- CÁC FIELD HỒ SƠ CÁ NHÂN ---
    @FXML private TextField usernameField;
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;

    // --- CÁC FIELD VÍ TIỀN ---
    @FXML private Label walletBalanceLabel;
    @FXML private TextField depositInput;
    @FXML private TableView<TransactionModel> txHistoryTable;
    @FXML private TableColumn<TransactionModel, String> colTxId;
    @FXML private TableColumn<TransactionModel, String> colTxType;
    @FXML private TableColumn<TransactionModel, String> colTxAmount;
    @FXML private TableColumn<TransactionModel, String> colTxTime;
    @FXML private TableColumn<TransactionModel, String> colTxStatus;

    private final ObservableList<TransactionModel> txList = FXCollections.observableArrayList();
    private double currentBalance = 15000000;

    @FXML
    public void initialize() {
        if (txHistoryTable != null) {
            if (colTxId != null) colTxId.setCellValueFactory(new PropertyValueFactory<>("id"));
            if (colTxType != null) colTxType.setCellValueFactory(new PropertyValueFactory<>("type"));
            if (colTxAmount != null) colTxAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
            if (colTxTime != null) colTxTime.setCellValueFactory(new PropertyValueFactory<>("time"));
            if (colTxStatus != null) colTxStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

            txHistoryTable.setItems(txList);
            txList.add(new TransactionModel("TX9012", "Nạp VietQR", "+5,000,000 VNĐ", "10:30 28/10", "Thành công", "Nạp VietQR"));
            txList.add(new TransactionModel("TX8821", "Đặt cọc phiên", "-2,000,000 VNĐ", "15:45 25/10", "Thành công", "Cọc phiên #AUC001"));
        }

        showProfileTab();
    }

    // =========================================================================
    // HÀM CHUYỂN TAB & ĐỔI MÀU NỀN BACKGROUND TỰ ĐỘNG
    // =========================================================================
    @FXML public void showProfileTab() { switchTab(true, false, false); }
    @FXML public void showWalletTab() { switchTab(false, true, false); }
    @FXML public void showSettingsTab() { switchTab(false, false, true); }

    @FXML public void handleSelectProfile() { showProfileTab(); }
    @FXML public void handleSelectWallet() { showWalletTab(); }
    @FXML public void handleSelectSettings() { showSettingsTab(); }
    @FXML public void navToWallet(ActionEvent event) { showWalletTab(); }

    private void switchTab(boolean profile, boolean wallet, boolean settings) {
        // 1. Ẩn/Hiện Pane nội dung tương ứng
        if (paneProfile != null) paneProfile.setVisible(profile);
        if (paneWallet != null) paneWallet.setVisible(wallet);
        if (paneSettings != null) paneSettings.setVisible(settings);

        // 2. Đổi màu nền -fX-background-color của các nút Tab
        String activeStyle = "-fx-background-color: #0284c7; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;";
        String inactiveStyle = "-fx-background-color: transparent; -fx-text-fill: #94a3b8; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;";

        if (btnTabProfile != null) btnTabProfile.setStyle(profile ? activeStyle : inactiveStyle);
        if (btnTabWallet != null) btnTabWallet.setStyle(wallet ? activeStyle : inactiveStyle);
        if (btnTabSettings != null) btnTabSettings.setStyle(settings ? activeStyle : inactiveStyle);
    }

    // =========================================================================
    // CÁC HÀM XỬ LÝ NÚT BẤM
    // =========================================================================
    @FXML public void handleSaveProfile() { System.out.println("✅ Đã lưu thông tin hồ sơ!"); }
    @FXML public void handleChangePassword() { System.out.println("🔑 Đã gửi yêu cầu đổi mật khẩu!"); }
    @FXML public void handleSaveSettings() { System.out.println("⚙️ Đã lưu cài đặt!"); }

    @FXML
    public void handleDepositQR() {
        if (depositInput == null) return;
        String inputStr = depositInput.getText().trim();
        if (inputStr.isEmpty()) return;

        try {
            double amount = Double.parseDouble(inputStr);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/auction/views/qr_deposit_modal.fxml"));
            Parent root = loader.load();

            QrDepositController controller = loader.getController();

            Stage popup = new Stage();
            popup.setTitle("Mã VietQR Nạp tiền");
            Stage mainStage = (Stage) paneProfile.getScene().getWindow();
            popup.initOwner(mainStage);
            popup.initModality(javafx.stage.Modality.WINDOW_MODAL);
            popup.setScene(new Scene(root));
            popup.setResizable(false);

            String username = (usernameField != null && !usernameField.getText().isEmpty()) ? usernameField.getText() : "USER";

            controller.setDepositData(amount, username, () -> {
                currentBalance += amount;
                if (walletBalanceLabel != null) {
                    walletBalanceLabel.setText(String.format("%,.0f", currentBalance) + " VNĐ");
                }
                txList.add(0, new TransactionModel(
                    "TX" + (int)(Math.random()*8999+1000), "Nạp QR", 
                    "+" + String.format("%,.0f", amount) + " VNĐ", "Vừa xong", "Thành công", "Nạp VietQR"
                ));
                depositInput.clear();
            });

            popup.showAndWait();

        } catch (NumberFormatException e) {
            System.err.println("Số tiền nhập không hợp lệ!");
        } catch (IOException e) {
            System.err.println("Lỗi mở Popup QR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- CÁC HÀM NAVBAR ĐIỀU HƯỚNG ---
    @FXML void navToHome(ActionEvent event) { switchScene("/com/auction/views/auction_list.fxml", "Trang chủ"); }
    @FXML void navToMyItems(ActionEvent event) { switchScene("/com/auction/views/seller_management.fxml", "Vật phẩm của tôi"); }
    @FXML void navToCreateAuction(ActionEvent event) { switchScene("/com/auction/views/create_auction.fxml", "Đăng bài bán & Tạo phiên"); }
    @FXML void navToProfile(ActionEvent event) { showProfileTab(); }
    @FXML void handleLogout(ActionEvent event) { switchScene("/com/auction/views/login.fxml", "Đăng nhập hệ thống"); }

    private void switchScene(String fxmlPath, String title) {
        try {
            Stage stage = (Stage) paneProfile.getScene().getWindow();
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

    public static class TransactionModel {
        private String id;
        private String type;
        private String amount;
        private String time;
        private String status;
        private String description;

        public TransactionModel(String id, String type, String amount, String time, String status, String description) {
            this.id = id;
            this.type = type;
            this.amount = amount;
            this.time = time;
            this.status = status;
            this.description = description;
        }

        public String getId() { return id; }
        public String getType() { return type; }
        public String getAmount() { return amount; }
        public String getTime() { return time; }
        public String getStatus() { return status; }
        public String getDescription() { return description; }
    }
}