package com.auction.client.controllers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LiveBiddingController {

    @FXML private Label timerLabel;
    @FXML private Label currentPriceLabel;
    @FXML private Label highestBidderLabel;
    @FXML private ListView<String> bidLogListView;
    @FXML private TextField bidAmountInput;
    @FXML private Label statusMsgLabel;

    // --- 1. KHAI BÁO FXML CHO AUTO-BIDDING (+0.5Đ) ---
    @FXML private TextField maxBidInput;
    @FXML private TextField stepInput;

    // --- 2. KHAI BÁO FXML CHO LINECHART (+0.5Đ) ---
    @FXML private LineChart<String, Number> priceChart;
    private final XYChart.Series<String, Number> priceSeries = new XYChart.Series<>();

    private final ObservableList<String> bidLogs = FXCollections.observableArrayList();
    private double currentPrice = 45500000;

    private Timeline countdownTimeline;
    private int timeRemainingSeconds = 320;

    @FXML
    public void initialize() {
        if (bidLogListView != null) {
            bidLogListView.setItems(bidLogs);
            bidLogs.add("🔥 [18:30:00] user_nam99 vừa đặt 45,500,000 VNĐ");
            bidLogs.add("⚡ [18:28:15] bidder_pro vừa đặt 45,000,000 VNĐ");
        }

        // Khởi tạo LineChart
        if (priceChart != null) {
            priceSeries.setName("Giá hiện tại");
            priceChart.getData().add(priceSeries);
            addPriceToChart("18:28:15", 45000000);
            addPriceToChart("18:30:00", 45500000);
        }

        startCountdown();
    }

    private void startCountdown() {
        countdownTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            timeRemainingSeconds--;

            if (timeRemainingSeconds <= 0) {
                timerLabel.setText("00:00");
                statusMsgLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                statusMsgLabel.setText("⛔ PHIÊN ĐẤU GIÁ ĐÃ KẾT THÚC!");
                bidAmountInput.setDisable(true);
                countdownTimeline.stop();
            } else {
                int minutes = timeRemainingSeconds / 60;
                int seconds = timeRemainingSeconds % 60;
                timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
            }
        }));

        countdownTimeline.setCycleCount(Timeline.INDEFINITE);
        countdownTimeline.play();
    }

    // =========================================================================
    // XỬ LÝ ĐẶT GIÁ & THUẬT TOÁN ANTI-SNIPING (+0.5Đ)
    // =========================================================================
    @FXML
    public void handlePlaceBid() {
        String inputStr = bidAmountInput.getText().trim();
        if (inputStr.isEmpty()) {
            statusMsgLabel.setText("⚠️ Vui lòng nhập số tiền!");
            return;
        }

        try {
            double bidVal = Double.parseDouble(inputStr);
            if (bidVal <= currentPrice) {
                statusMsgLabel.setStyle("-fx-text-fill: #ef4444;");
                statusMsgLabel.setText("⚠️ Số tiền phải lớn hơn giá hiện tại!");
                return;
            }

            // --- THUẬT TOÁN ANTI-SNIPING (+0.5Đ) ---
            // Nếu đặt giá trong 10 giây cuối -> Tự động gia hạn thêm 60 giây (1 phút)
            if (timeRemainingSeconds <= 10 && timeRemainingSeconds > 0) {
                timeRemainingSeconds += 60;
                statusMsgLabel.setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: bold;");
                statusMsgLabel.setText("🔥 Anti-Sniping kích hoạt! Gia hạn thêm 60 giây.");
            } else {
                statusMsgLabel.setStyle("-fx-text-fill: #10b981;");
                statusMsgLabel.setText("✅ Đặt giá thành công!");
            }

            currentPrice = bidVal;
            currentPriceLabel.setText(String.format("%,.0f", currentPrice) + " VNĐ");
            highestBidderLabel.setText("Người giữ giá: Bạn (Tôi)");

            String currentTimeStr = new SimpleDateFormat("HH:mm:ss").format(new Date());
            bidLogs.add(0, "🎉 [" + currentTimeStr + "] Bạn đã trả giá " + String.format("%,.0f", currentPrice) + " VNĐ");

            // Vẽ điểm mới lên Biểu đồ LineChart
            addPriceToChart(currentTimeStr, currentPrice);

            bidAmountInput.clear();

        } catch (NumberFormatException e) {
            statusMsgLabel.setStyle("-fx-text-fill: #ef4444;");
            statusMsgLabel.setText("⚠️ Định dạng số tiền không hợp lệ!");
        }
    }

    // =========================================================================
    // TÍNH NĂNG NÂNG CAO 1: CÀI ĐẶT AUTO-BIDDING (+0.5Đ)
    // =========================================================================
    @FXML
    private void handleEnableAutoBid() {
        String maxBidStr = maxBidInput.getText().trim();
        String stepStr = stepInput.getText().trim();

        if (maxBidStr.isEmpty() || stepStr.isEmpty()) {
            statusMsgLabel.setStyle("-fx-text-fill: #ef4444;");
            statusMsgLabel.setText("⚠️ Vui lòng nhập đầy đủ MaxBid và Bước giá!");
            return;
        }

        try {
            double maxBid = Double.parseDouble(maxBidStr);
            double step = Double.parseDouble(stepStr);

            if (maxBid <= currentPrice) {
                statusMsgLabel.setStyle("-fx-text-fill: #ef4444;");
                statusMsgLabel.setText("⚠️ MaxBid phải lớn hơn giá hiện tại!");
                return;
            }

            statusMsgLabel.setStyle("-fx-text-fill: #38bdf8; -fx-font-weight: bold;");
            statusMsgLabel.setText("🤖 Đã kích hoạt Auto-Bid thành công (Max: " + String.format("%,.0f", maxBid) + " VNĐ)");

            // Gửi dữ liệu cài đặt Auto-Bid tới Backend (Socket)
            // TODO: socketClient.send("AUTO_BID|" + maxBid + "|" + step);

        } catch (NumberFormatException e) {
            statusMsgLabel.setStyle("-fx-text-fill: #ef4444;");
            statusMsgLabel.setText("⚠️ Định dạng MaxBid hoặc Bước giá không hợp lệ!");
        }
    }

    // =========================================================================
    // TÍNH NĂNG NÂNG CAO 3: BIỂU ĐỒ LINECHART THỜI GIAN THỰC (+0.5Đ)
    // =========================================================================
    public void addPriceToChart(String timeLabel, double price) {
        Platform.runLater(() -> {
            if (priceSeries != null) {
                priceSeries.getData().add(new XYChart.Data<>(timeLabel, price));
            }
        });
    }

    // --- CÁC HÀM NAVBAR ĐIỀU HƯỚNG ---
    @FXML void navToHome(ActionEvent event) { switchScene("/com/auction/views/auction_list.fxml", "Trang chủ"); }
    @FXML void navToMyItems(ActionEvent event) { switchScene("/com/auction/views/seller_management.fxml", "Vật phẩm của tôi"); }
    @FXML void navToCreateAuction(ActionEvent event) { switchScene("/com/auction/views/create_auction.fxml", "Đăng bài bán & Tạo phiên"); }
    @FXML void navToWallet(ActionEvent event) { switchScene("/com/auction/views/user_profile.fxml", "Ví tiền"); }
    @FXML void navToProfile(ActionEvent event) { switchScene("/com/auction/views/user_profile.fxml", "Hồ sơ cá nhân"); }
    @FXML void handleLogout(ActionEvent event) { switchScene("/com/auction/views/login.fxml", "Đăng nhập hệ thống"); }

    private void switchScene(String fxmlPath, String title) {
        if (countdownTimeline != null) {
            countdownTimeline.stop();
        }

        try {
            Stage stage = (Stage) timerLabel.getScene().getWindow();
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