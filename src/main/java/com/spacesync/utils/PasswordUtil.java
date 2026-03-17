package com.spacesync.utils;

import java.security.MessageDigest;
import java.util.Formatter;

public class PasswordUtil {
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            // Use UTF-8 to ensure consistency across different systems
            byte[] hash = md.digest(password.getBytes("UTF-8"));
            Formatter formatter = new Formatter();
            for (byte b : hash) {
                formatter.format("%02x", b);
            }
            String result = formatter.toString();
            formatter.close();
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
}