package com.auction.client.controllers;

import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class AdminDashboardController {

    // --- NÚT THỐNG KÊ ---
    @FXML private Label totalUsersLabel;
    @FXML private Label totalAuctionsLabel;
    @FXML private Label totalRevenueLabel;

    // --- BẢNG DUYỆT PHIÊN ĐẤU GIÁ ---
    @FXML private TableView<PendingAuctionModel> pendingAuctionTable;
    @FXML private TableColumn<PendingAuctionModel, String> colAuctionId;
    @FXML private TableColumn<PendingAuctionModel, String> colSellerName;
    @FXML private TableColumn<PendingAuctionModel, String> colItemTitle;
    @FXML private TableColumn<PendingAuctionModel, String> colStartPrice;

    private final ObservableList<PendingAuctionModel> pendingList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        if (totalUsersLabel != null) totalUsersLabel.setText("128");
        if (totalAuctionsLabel != null) totalAuctionsLabel.setText("45");
        if (totalRevenueLabel != null) totalRevenueLabel.setText("320,000,000 VNĐ");

        // Cấu hình bảng duyệt bài
        if (pendingAuctionTable != null) {
            colAuctionId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colSellerName.setCellValueFactory(new PropertyValueFactory<>("seller"));
            colItemTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
            colStartPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

            pendingAuctionTable.setItems(pendingList);
            pendingList.add(new PendingAuctionModel("AUC102", "TranVanB", "SH350i 2024 Lướt", "120,000,000 VNĐ"));
            pendingList.add(new PendingAuctionModel("AUC103", "LeThiC", "MacBook Pro M3 Max", "65,000,000 VNĐ"));
        }
    }

    // --- XỬ LÝ DUYỆT/TỪ CHỐI BÀI ---
    @FXML
    public void handleApproveAuction() {
        PendingAuctionModel selected = pendingAuctionTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            pendingList.remove(selected);
            System.out.println("✅ Đã duyệt phiên đấu giá: " + selected.getId());
        }
    }

    @FXML
    public void handleRejectAuction() {
        PendingAuctionModel selected = pendingAuctionTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            pendingList.remove(selected);
            System.out.println("❌ Đã từ chối phiên đấu giá: " + selected.getId());
        }
    }

    // --- ĐĂNG XUẤT HỆ THỐNG ---
    @FXML
    public void handleLogout(ActionEvent event) {
        try {
            Stage stage = (Stage) totalUsersLabel.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/com/auction/views/login.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("Đăng nhập hệ thống");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- INNER MODEL ---
    public static class PendingAuctionModel {
        private String id;
        private String seller;
        private String title;
        private String price;

        public PendingAuctionModel(String id, String seller, String title, String price) {
            this.id = id;
            this.seller = seller;
            this.title = title;
            this.price = price;
        }

        public String getId() { return id; }
        public String getSeller() { return seller; }
        public String getTitle() { return title; }
        public String getPrice() { return price; }
    }
}