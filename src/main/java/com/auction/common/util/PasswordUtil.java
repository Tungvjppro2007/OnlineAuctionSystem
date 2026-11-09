package com.auction.common.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Tiện ích mã hóa mật khẩu SHA-256.
 * Dùng java.security.MessageDigest (native JDK, không cần dependency).
 */
public class PasswordUtil {

    /**
     * Hash mật khẩu bằng SHA-256, trả về chuỗi hex 64 ký tự.
     */
    public static String hashPassword(String plainPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(
                plainPassword.getBytes(StandardCharsets.UTF_8)
            );
            // Chuyển byte[] → hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 không khả dụng!", e);
        }
    }

    /**
     * So sánh mật khẩu nhập vào với hash đã lưu trong DB.
     */
    public static boolean verifyPassword(String plainPassword, String storedHash) {
        return hashPassword(plainPassword).equals(storedHash);
    }
}