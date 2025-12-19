package com.example.musicapplication.constants;

/**
 * Application-wide constants
 * Contains limits, timeouts, formats, validation rules, and messages
 */
public class AppConstants {

    // ==================== QUERY LIMITS ====================
    public static final int TRENDING_SONGS_LIMIT = 10;
    public static final int NEW_SONGS_LIMIT = 20;
    public static final int SEARCH_RESULTS_LIMIT = 50;
    public static final int ALBUM_SONGS_LIMIT = 100;
    public static final int PLAYLIST_SONGS_LIMIT = 100;
    public static final int HISTORY_LIMIT = 50;

    // ==================== TIMEOUTS ====================
    public static final int SEARCH_DEBOUNCE_MS = 300;
    public static final int SLIDER_AUTO_SCROLL_MS = 3000;
    public static final int NETWORK_TIMEOUT_MS = 10000;
    public static final int UPLOAD_TIMEOUT_MS = 60000;

    // ==================== FORMATS ====================
    public static final String TIME_FORMAT = "mm:ss";
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final String DATETIME_FORMAT = "dd/MM/yyyy HH:mm";

    // ==================== VALIDATION RULES ====================
    // Password
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int MAX_PASSWORD_LENGTH = 50;

    // Song
    public static final int MIN_SONG_TITLE_LENGTH = 1;
    public static final int MAX_SONG_TITLE_LENGTH = 100;
    public static final int MAX_ARTIST_NAME_LENGTH = 100;
    public static final int MAX_ALBUM_NAME_LENGTH = 100;

    // Playlist
    public static final int MIN_PLAYLIST_NAME_LENGTH = 1;
    public static final int MAX_PLAYLIST_NAME_LENGTH = 50;
    public static final int MAX_PLAYLIST_DESCRIPTION_LENGTH = 200;

    // User Profile
    public static final int MAX_USERNAME_LENGTH = 30;
    public static final int MAX_BIO_LENGTH = 200;
    public static final int MAX_DISPLAY_NAME_LENGTH = 50;

    // File Size (in bytes)
    public static final long MAX_AUDIO_FILE_SIZE = 50 * 1024 * 1024; // 50 MB
    public static final long MAX_IMAGE_FILE_SIZE = 5 * 1024 * 1024;  // 5 MB

    // ==================== ERROR MESSAGES ====================
    public static final String ERROR_NETWORK = "Không có kết nối mạng";
    public static final String ERROR_UNKNOWN = "Đã xảy ra lỗi";
    public static final String ERROR_UPLOAD_FAILED = "Upload thất bại";
    public static final String ERROR_LOAD_FAILED = "Không thể tải dữ liệu";
    public static final String ERROR_PERMISSION_DENIED = "Không có quyền truy cập";
    public static final String ERROR_FILE_TOO_LARGE = "File quá lớn";
    public static final String ERROR_INVALID_FILE = "File không hợp lệ";

    // ==================== SUCCESS MESSAGES ====================
    public static final String SUCCESS_UPLOAD = "Upload thành công";
    public static final String SUCCESS_SAVE = "Lưu thành công";
    public static final String SUCCESS_DELETE = "Xóa thành công";
    public static final String SUCCESS_UPDATE = "Cập nhật thành công";

    // ==================== INFO MESSAGES ====================
    public static final String INFO_LOADING = "Đang tải...";
    public static final String INFO_UPLOADING = "Đang upload...";
    public static final String INFO_PROCESSING = "Đang xử lý...";

    // ==================== CACHE SETTINGS ====================
    public static final int IMAGE_CACHE_SIZE_MB = 50;
    public static final int DISK_CACHE_SIZE_MB = 100;

    // ==================== PLAYER SETTINGS ====================
    public static final int SEEK_FORWARD_MS = 10000;  // 10 seconds
    public static final int SEEK_BACKWARD_MS = 10000; // 10 seconds
    public static final int UPDATE_INTERVAL_MS = 1000; // 1 second
}
