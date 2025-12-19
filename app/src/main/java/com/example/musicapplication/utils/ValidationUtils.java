package com.example.musicapplication.utils;

import android.text.TextUtils;
import android.util.Patterns;

import com.example.musicapplication.constants.AppConstants;

/**
 * Utility class for input validation
 * Provides methods to validate user inputs
 */
public class ValidationUtils {
    
    /**
     * Kiểm tra email hợp lệ
     */
    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    
    /**
     * Kiểm tra password hợp lệ (tối thiểu 6 ký tự)
     */
    public static boolean isValidPassword(String password) {
        return !TextUtils.isEmpty(password) && password.length() >= AppConstants.MIN_PASSWORD_LENGTH;
    }
    
    /**
     * Kiểm tra chuỗi không rỗng
     */
    public static boolean isNotEmpty(String text) {
        return !TextUtils.isEmpty(text) && !text.trim().isEmpty();
    }
    
    /**
     * Kiểm tra tên bài hát hợp lệ
     */
    public static boolean isValidSongTitle(String title) {
        return isNotEmpty(title) && title.length() <= AppConstants.MAX_SONG_TITLE_LENGTH;
    }
    
    /**
     * Kiểm tra tên playlist hợp lệ
     */
    public static boolean isValidPlaylistName(String name) {
        return isNotEmpty(name) && name.length() <= AppConstants.MAX_PLAYLIST_NAME_LENGTH;
    }
    
    /**
     * Kiểm tra bio hợp lệ
     */
    public static boolean isValidBio(String bio) {
        return bio == null || bio.length() <= AppConstants.MAX_BIO_LENGTH;
    }
    
    /**
     * Kiểm tra URL hợp lệ
     */
    public static boolean isValidUrl(String url) {
        return !TextUtils.isEmpty(url) && Patterns.WEB_URL.matcher(url).matches();
    }
    
    /**
     * Kiểm tra số điện thoại hợp lệ
     */
    public static boolean isValidPhoneNumber(String phone) {
        return !TextUtils.isEmpty(phone) && Patterns.PHONE.matcher(phone).matches();
    }
    
    /**
     * Lấy thông báo lỗi cho email
     */
    public static String getEmailError(String email) {
        if (TextUtils.isEmpty(email)) {
            return "Email không được để trống";
        }
        if (!isValidEmail(email)) {
            return "Email không hợp lệ";
        }
        return null;
    }
    
    /**
     * Lấy thông báo lỗi cho password
     */
    public static String getPasswordError(String password) {
        if (TextUtils.isEmpty(password)) {
            return "Mật khẩu không được để trống";
        }
        if (password.length() < AppConstants.MIN_PASSWORD_LENGTH) {
            return "Mật khẩu phải có ít nhất " + AppConstants.MIN_PASSWORD_LENGTH + " ký tự";
        }
        return null;
    }
    
    /**
     * Lấy thông báo lỗi cho tên bài hát
     */
    public static String getSongTitleError(String title) {
        if (TextUtils.isEmpty(title) || title.trim().isEmpty()) {
            return "Tên bài hát không được để trống";
        }
        if (title.length() > AppConstants.MAX_SONG_TITLE_LENGTH) {
            return "Tên bài hát quá dài (tối đa " + AppConstants.MAX_SONG_TITLE_LENGTH + " ký tự)";
        }
        return null;
    }
    
    /**
     * Lấy thông báo lỗi cho tên playlist
     */
    public static String getPlaylistNameError(String name) {
        if (TextUtils.isEmpty(name) || name.trim().isEmpty()) {
            return "Tên playlist không được để trống";
        }
        if (name.length() > AppConstants.MAX_PLAYLIST_NAME_LENGTH) {
            return "Tên playlist quá dài (tối đa " + AppConstants.MAX_PLAYLIST_NAME_LENGTH + " ký tự)";
        }
        return null;
    }
}

