package com.auction.client.controllers;

import java.io.IOException;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class AuctionListController {

    @FXML private Label welcomeUserLabel;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> categoryFilterComboBox;
    @FXML private ComboBox<String> statusFilterComboBox;
    @FXML private GridPane itemGridContainer;

    private final ObservableList<AuctionItemModel> masterAuctionList = FXCollections.observableArrayList();
    private FilteredList<AuctionItemModel> filteredData;

    @FXML
    public void initialize() {
        // 1. Khởi tạo danh sách tùy chọn cho ComboBox bộ lọc
        if (categoryFilterComboBox != null) {
            categoryFilterComboBox.setItems(FXCollections.observableArrayList(
                "Tất cả danh mục", "Thiết bị điện tử", "Xe cộ", "Tranh nghệ thuật", "Trang sức"
            ));
            categoryFilterComboBox.setValue("Tất cả danh mục");
        }

        if (statusFilterComboBox != null) {
            statusFilterComboBox.setItems(FXCollections.observableArrayList(
                "Đang diễn ra", "Sắp diễn ra", "Đã kết thúc"
            ));
            statusFilterComboBox.setValue("Đang diễn ra");
        }

        // 2. Nạp dữ liệu sản phẩm mẫu có sẵn lên trang chủ
        loadSampleData();

        // 3. Đăng ký sự kiện lọc & tìm kiếm thời gian thực
        setupSearchAndFilter();
    }

    // Nạp dữ liệu sản phẩm mẫu hiển thị ngay khi ứng dụng khởi chạy
    private void loadSampleData() {
        masterAuctionList.clear();

        masterAuctionList.add(new AuctionItemModel("AUC001", "Laptop MacBook Pro M3 Max", "Thiết bị điện tử", "45,000,000", "user_nam99", "05:20 min", "/com/auction/images/uet_logo.png"));
        masterAuctionList.add(new AuctionItemModel("AUC002", "Xe máy Vespa Vintage 1970", "Xe cộ", "82,500,000", "bidder_pro", "12:45 min", "/com/auction/images/uet_logo.png"));
        masterAuctionList.add(new AuctionItemModel("AUC003", "Bức tranh 'Phố Cổ Hà Nội'", "Tranh nghệ thuật", "15,000,000", "hoasi_ha", "01:10 min", "/com/auction/images/uet_logo.png"));
        masterAuctionList.add(new AuctionItemModel("AUC004", "Đồng hồ Rolex Submariner", "Trang sức", "210,000,000", "cuong_dollar", "45:00 min", "/com/auction/images/uet_logo.png"));

        filteredData = new FilteredList<>(masterAuctionList, p -> true);

        // Render toàn bộ thẻ sản phẩm lên giao diện GridPane
        renderCards(filteredData);
    }

    // Hàm duyệt danh sách và vẽ các thẻ sản phẩm lên GridPane
    private void renderCards(List<AuctionItemModel> items) {
        if (itemGridContainer == null) return;

        itemGridContainer.getChildren().clear();

        int columns = 3; // Hiển thị 3 sản phẩm trên 1 hàng
        int row = 0;
        int col = 0;

        for (AuctionItemModel item : items) {
            VBox card = createProductCard(item);
            itemGridContainer.add(card, col, row);

            col++;
            if (col >= columns) {
                col = 0;
                row++;
            }
        }
    }

    // Hàm tạo 1 thẻ sản phẩm (Card) tự động phình to theo ô lưới
    private VBox createProductCard(AuctionItemModel item) {
        VBox card = new VBox(10);
        
        // Cho phép Card tự động giãn hết chiều rộng ô GridPane chứa nó
        card.setMaxWidth(Double.MAX_VALUE);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 15; -fx-cursor: hand;");

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.08));
        shadow.setRadius(10);
        shadow.setOffsetY(4);
        card.setEffect(shadow);

        // Khung chứa ảnh đại diện
        ImageView imageView = new ImageView();
        try {
            if (item.getImagePath() != null) {
                Image img = new Image(getClass().getResourceAsStream(item.getImagePath()));
                imageView.setImage(img);
            }
        } catch (Exception e) {
            // Trường hợp nạp tệp ảnh bị lỗi
        }
        imageView.setFitWidth(220);
        imageView.setFitHeight(140);
        imageView.setPreserveRatio(true);

        HBox imageHolder = new HBox(imageView);
        imageHolder.setAlignment(Pos.CENTER);
        imageHolder.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 8; -fx-padding: 5;");

        // Nhãn tiêu đề, giá tiền & người giữ giá
        Label titleLabel = new Label(item.getName());
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        titleLabel.setTextFill(Color.web("#0f172a"));

        Label priceLabel = new Label(item.getPrice() + " VNĐ");
        priceLabel.setFont(Font.font("System", FontWeight.BOLD, 15));
        priceLabel.setTextFill(Color.web("#0052cc"));

        Label infoLabel = new Label("Người giữ giá: " + item.getHighestBidder() + "\nCòn lại: " + item.getTimeLeft());
        infoLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;");

        Button actionBtn = new Button("🔨 Tham gia đấu giá");
        actionBtn.setMaxWidth(Double.MAX_VALUE);
        actionBtn.setStyle("-fx-background-color: #0052cc; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 8; -fx-cursor: hand;");

        // Đăng ký sự kiện mở trang Chi tiết sản phẩm
        actionBtn.setOnAction(e -> openItemDetailScreen(item));
        card.setOnMouseClicked(e -> openItemDetailScreen(item));

        card.getChildren().addAll(imageHolder, titleLabel, priceLabel, infoLabel, actionBtn);
        return card;
    }

    // Mở trang Chi tiết sản phẩm (Giữ nguyên trạng thái Phóng to/Maximize của cửa sổ)
    private void openItemDetailScreen(AuctionItemModel item) {
        try {
            Stage stage = (Stage) searchField.getScene().getWindow();
            
            // 1. Lưu lại trạng thái phóng to màn hình hiện tại
            boolean isMaximized = stage.isMaximized();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/auction/views/item_detail.fxml"));
            Parent root = loader.load();

            // 2. Truyền dữ liệu sang Controller trang Chi tiết
            ItemDetailController controller = loader.getController();
            controller.setItemData(item.getId(), item.getName(), item.getCategory(), item.getPrice());

            // 3. Tạo Scene không truyền kích thước cố định để tự động co giãn theo Stage
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Chi tiết sản phẩm - " + item.getName());

            // 4. Khôi phục lại trạng thái Maximize
            stage.setMaximized(isMaximized);
            stage.show();

        } catch (IOException e) {
            System.err.println("Lỗi khi mở file item_detail.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupSearchAndFilter() {
        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        }
        if (categoryFilterComboBox != null) {
            categoryFilterComboBox.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        }
    }

    private void applyFilters() {
        if (filteredData == null) return;

        String searchText = (searchField == null || searchField.getText() == null) ? "" : searchField.getText().toLowerCase().trim();
        String selectedCategory = categoryFilterComboBox == null ? null : categoryFilterComboBox.getValue();

        filteredData.setPredicate(item -> {
            boolean matchesSearch = searchText.isEmpty() 
                    || item.getName().toLowerCase().contains(searchText)
                    || item.getId().toLowerCase().contains(searchText);

            boolean matchesCategory = selectedCategory == null 
                    || selectedCategory.equals("Tất cả danh mục") 
                    || item.getCategory().equalsIgnoreCase(selectedCategory);

            return matchesSearch && matchesCategory;
        });

        renderCards(filteredData);
    }

    // --- CÁC HÀM ĐIỀU HƯỚNG NAVBAR (Duy trì phóng to màn hình) ---

    @FXML void navToHome(ActionEvent event) { loadSampleData(); }
    @FXML void navToMyItems(ActionEvent event) { switchScene("/com/auction/views/seller_management.fxml", "Vật phẩm của tôi"); }
    @FXML
    void navToCreateAuction(ActionEvent event) {
        switchScene("/com/auction/views/create_auction.fxml", "Đăng bài bán & Tạo phiên mới");
    }
    @FXML void navToWallet(ActionEvent event) { switchScene("/com/auction/views/user_profile.fxml", "Ví tiền"); }
    @FXML void navToProfile(ActionEvent event) { switchScene("/com/auction/views/user_profile.fxml", "Hồ sơ cá nhân"); }
    @FXML void handleLogout(ActionEvent event) { switchScene("/com/auction/views/login.fxml", "Đăng nhập"); }
    @FXML void handleRefresh(ActionEvent event) { if (searchField != null) searchField.clear(); loadSampleData(); }

    // Hàm chuyển cảnh dùng chung - Giữ nguyên cửa sổ Maximize
    private void switchScene(String fxmlPath, String title) {
        try {
            Stage stage = (Stage) searchField.getScene().getWindow();
            
            boolean isMaximized = stage.isMaximized();

            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(title);

            stage.setMaximized(isMaximized);
            stage.show();

        } catch (IOException e) {
            System.err.println("Chưa thể nạp màn hình: " + fxmlPath + " - " + e.getMessage());
        }
    }

    // --- INNER CLASS MODEL ĐẠI DIỆN DỮ LIỆU VẬT PHẨM ---

    public static class AuctionItemModel {
        private String id;
        private String name;
        private String category;
        private String price;
        private String highestBidder;
        private String timeLeft;
        private String imagePath;

        public AuctionItemModel(String id, String name, String category, String price, String highestBidder, String timeLeft, String imagePath) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.price = price;
            this.highestBidder = highestBidder;
            this.timeLeft = timeLeft;
            this.imagePath = imagePath;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public String getCategory() { return category; }
        public String getPrice() { return price; }
        public String getHighestBidder() { return highestBidder; }
        public String getTimeLeft() { return timeLeft; }
        public String getImagePath() { return imagePath; }
    }
}