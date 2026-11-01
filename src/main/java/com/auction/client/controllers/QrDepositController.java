package com.auction.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class QrDepositController {

    @FXML private ImageView qrImageView;
    @FXML private Label amountLabel;
    @FXML private Label memoLabel;

    private Runnable onCompleteCallback;

    public void setDepositData(double amount, String username, Runnable onComplete) {
        this.onCompleteCallback = onComplete;
        String formattedAmount = String.format("%,.0f", amount) + " VNĐ";
        amountLabel.setText(formattedAmount);

        String memo = "NAP UETAUCTION " + username.toUpperCase();
        memoLabel.setText(memo);

        // --- CẤU HÌNH THÔNG TIN TÀI KHOẢN CỦA BẠN TẠI ĐÂY ---
        String bankId = "MB";             // Thay mã ngân hàng của bạn (VD: MB, VCB, TCB, BIDV...)
        String accountNo = "0332611949";   // Thay bằng SỐ TÀI KHOẢN CỦA BẠN

        // Đường dẫn tự động sinh mã QR nhận tiền chuẩn ngân hàng
        String qrUrl = String.format(
            "https://img.vietqr.io/image/%s-%s-compact2.png?amount=%.0f&addInfo=%s",
            bankId, accountNo, amount, memo.replace(" ", "%20")
        );

        try {
            Image qrImage = new Image(qrUrl, true);
            qrImageView.setImage(qrImage);
        } catch (Exception e) {
            System.err.println("Không thể nạp mã QR: " + e.getMessage());
        }
    }

    @FXML
    public void handleClose() {
        if (onCompleteCallback != null) {
            onCompleteCallback.run(); // Thực thi callback cộng tiền ở WalletController
        }
        Stage stage = (Stage) amountLabel.getScene().getWindow();
        stage.close();
    }
}