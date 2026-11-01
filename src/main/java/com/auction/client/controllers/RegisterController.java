package com.auction.client.controllers;
 
import java.io.IOException;
import java.net.URL;
import java.util.Set;
import java.util.regex.Pattern;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;
 
public class RegisterController {
 
    private static final String ROLE_BIDDER = "Người đấu giá";
    private static final String ROLE_SELLER = "Người bán";
 
    private static final Pattern USERNAME = Pattern.compile("^[a-zA-Z0-9_]{4,20}$");
    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    private static final int MIN_PASSWORD_LENGTH = 6;
 
    @FXML private TextField fullNameField;
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private Label statusLabel;
    @FXML private Button registerButton;
 
    @FXML
    public void initialize() {
        // Admin không tự đăng ký được, chỉ có Bidder và Seller
        roleComboBox.setItems(FXCollections.observableArrayList(ROLE_BIDDER, ROLE_SELLER));
        roleComboBox.getSelectionModel().selectFirst();
 
        // Xóa thông báo khi người dùng gõ lại
        for (TextField field : new TextField[] {
            fullNameField, usernameField, emailField, passwordField, confirmField}) {
            field.textProperty().addListener((obs, oldText, newText) -> clearStatus());
        }
    }
 
    @FXML
    public void handleRegister() {
        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText(); // không trim mật khẩu
        String confirm = confirmField.getText();
        String role = ROLE_SELLER.equals(roleComboBox.getValue()) ? "SELLER" : "BIDDER";
 
        String error = validate(fullName, username, email, password, confirm);
        if (error != null) {
            showError("⚠️ " + error);
            return;
        }
 
        setBusy(true);
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                // TODO: thay bằng gửi REGISTER qua Socket; Server băm mật khẩu rồi mới lưu
                registerOnServer(fullName, username, email, password, role);
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            showSuccess("🎉 Đăng ký thành công! Đang chuyển về trang đăng nhập...");
            PauseTransition delay = new PauseTransition(Duration.seconds(1.2));
            delay.setOnFinished(ev -> goToLogin());
            delay.play();
        });
        task.setOnFailed(e -> {
            showError("❌ " + task.getException().getMessage());
            setBusy(false);
        });
 
        Thread thread = new Thread(task, "register-task");
        thread.setDaemon(true);
        thread.start();
    }
 
    @FXML
    public void handleGoToLogin() {
        goToLogin();
    }
 
    /** Trả về thông báo lỗi đầu tiên tìm thấy, hoặc null nếu hợp lệ. */
    private String validate(String fullName, String username, String email,
                            String password, String confirm) {
        if (fullName.isEmpty() || username.isEmpty() || email.isEmpty()
                || password.isEmpty() || confirm.isEmpty()) {
            return "Vui lòng điền đầy đủ tất cả các trường.";
        }
        if (!USERNAME.matcher(username).matches()) {
            return "Tên đăng nhập gồm 4-20 ký tự: chữ, số hoặc dấu gạch dưới.";
        }
        if (!EMAIL.matcher(email).matches()) {
            return "Email không đúng định dạng.";
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            return "Mật khẩu phải có ít nhất " + MIN_PASSWORD_LENGTH + " ký tự.";
        }
        if (!password.equals(confirm)) {
            return "Mật khẩu nhập lại không khớp.";
        }
        return null;
    }
 
    /** Demo: coi như Server đã có sẵn các tên này. Sau này thay bằng Server thật. */
    private void registerOnServer(String fullName, String username, String email,
                                  String password, String role) throws Exception {
        Set<String> taken = Set.of("admin", "bidder", "seller");
        if (taken.contains(username.toLowerCase())) {
            throw new Exception("Tên đăng nhập đã được sử dụng!");
        }
    }
 
    private void goToLogin() {
        URL url = getClass().getResource("/com/auction/views/login.fxml");
        if (url == null) {
            showError("⚠️ Không tìm thấy giao diện đăng nhập.");
            setBusy(false);
            return;
        }
        try {
            Stage stage = (Stage) registerButton.getScene().getWindow();
            Parent root = FXMLLoader.load(url);
            stage.setScene(new Scene(root, 950, 650));
            stage.setTitle("Đăng nhập - Online Auction System");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showError("⚠️ Không thể tải giao diện đăng nhập.");
            setBusy(false);
        }
    }
 
    private void setBusy(boolean busy) {
        registerButton.setDisable(busy);
        fullNameField.setDisable(busy);
        usernameField.setDisable(busy);
        emailField.setDisable(busy);
        passwordField.setDisable(busy);
        confirmField.setDisable(busy);
        roleComboBox.setDisable(busy);
        registerButton.setText(busy ? "Đang đăng ký..." : "Đăng ký");
    }
 
    private void showError(String message) {
        statusLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
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