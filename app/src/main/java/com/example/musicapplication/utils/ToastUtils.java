package com.example.musicapplication.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Utility class for displaying Toast messages
 * Provides consistent toast messages with emoji prefixes
 */
public class ToastUtils {
    
    /**
     * Hiển thị Toast ngắn
     */
    public static void showShort(Context context, String message) {
        if (context != null && message != null) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Hiển thị Toast dài
     */
    public static void showLong(Context context, String message) {
        if (context != null && message != null) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    }
    
    /**
     * Hiển thị Toast lỗi (với emoji ❌)
     */
    public static void showError(Context context, String message) {
        if (context != null && message != null) {
            Toast.makeText(context, "❌ " + message, Toast.LENGTH_LONG).show();
        }
    }
    
    /**
     * Hiển thị Toast thành công (với emoji ✅)
     */
    public static void showSuccess(Context context, String message) {
        if (context != null && message != null) {
            Toast.makeText(context, "✅ " + message, Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Hiển thị Toast cảnh báo (với emoji ⚠️)
     */
    public static void showWarning(Context context, String message) {
        if (context != null && message != null) {
            Toast.makeText(context, "⚠️ " + message, Toast.LENGTH_LONG).show();
        }
    }
    
    /**
     * Hiển thị Toast thông tin (với emoji ℹ️)
     */
    public static void showInfo(Context context, String message) {
        if (context != null && message != null) {
            Toast.makeText(context, "ℹ️ " + message, Toast.LENGTH_SHORT).show();
        }
    }
}

