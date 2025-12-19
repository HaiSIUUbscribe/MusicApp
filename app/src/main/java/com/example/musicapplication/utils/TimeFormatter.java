package com.example.musicapplication.utils;

import java.util.Locale;

/**
 * Utility class for formatting time and numbers
 * Provides methods to format durations, play counts, and timestamps
 */
public class TimeFormatter {
    
    /**
     * Format milliseconds thành mm:ss
     */
    public static String formatDuration(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }
    
    /**
     * Format play count (1000 → 1K, 1000000 → 1M)
     */
    public static String formatPlayCount(int count) {
        if (count < 1000) return String.valueOf(count);
        if (count < 1000000) return String.format(Locale.US, "%.1fK", count / 1000.0);
        return String.format(Locale.US, "%.1fM", count / 1000000.0);
    }
    
    /**
     * Format timestamp thành "2 giờ trước", "3 ngày trước"
     */
    public static String formatTimeAgo(long timestamp) {
        long now = System.currentTimeMillis();
        long diff = now - timestamp;
        
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        
        if (days > 0) return days + " ngày trước";
        if (hours > 0) return hours + " giờ trước";
        if (minutes > 0) return minutes + " phút trước";
        return "Vừa xong";
    }
}
