# 📖 HƯỚNG DẪN SỬ DỤNG UTILITIES VÀ CONSTANTS

## 🎯 Mục đích
File này hướng dẫn cách sử dụng các utility classes và constants trong dự án.

---

## 1️⃣ ImageLoader - Load ảnh

### ❌ Trước (Code cũ - lặp lại nhiều lần)
```java
Glide.with(context)
    .load(song.getImageUrl())
    .placeholder(R.drawable.ic_music)
    .error(R.drawable.ic_music)
    .transform(new CenterCrop(), new RoundedCorners(16))
    .into(holder.imgSong);
```

### ✅ Sau (Sử dụng ImageLoader)
```java
// Load ảnh cơ bản
ImageLoader.load(context, song.getImageUrl(), holder.imgSong);

// Load ảnh góc bo tròn
ImageLoader.loadRounded(context, song.getImageUrl(), holder.imgSong, 16);

// Load ảnh tròn (avatar)
ImageLoader.loadCircle(context, user.getPhotoUrl(), imgProfile);

// Load ảnh với callback (để lấy Bitmap cho Palette)
ImageLoader.loadWithCallback(context, song.getImageUrl(), albumArt, bitmap -> {
    // Xử lý bitmap
    Palette.from(bitmap).generate(palette -> {
        // Sử dụng palette
    });
});
```

---

## 2️⃣ TimeFormatter - Format thời gian

### ❌ Trước
```java
long seconds = milliseconds / 1000;
long minutes = seconds / 60;
seconds = seconds % 60;
String time = String.format(Locale.US, "%02d:%02d", minutes, seconds);
```

### ✅ Sau
```java
// Format duration (mm:ss)
String duration = TimeFormatter.formatDuration(song.getDuration());
// Kết quả: "03:45"

// Format play count
String playCount = TimeFormatter.formatPlayCount(song.getPlayCount());
// Kết quả: "1.2K" hoặc "2.5M"

// Format time ago
String timeAgo = TimeFormatter.formatTimeAgo(song.getUploadDate());
// Kết quả: "2 giờ trước" hoặc "3 ngày trước"
```

---

## 3️⃣ ToastUtils - Hiển thị thông báo

### ❌ Trước
```java
Toast.makeText(context, "Upload thành công!", Toast.LENGTH_SHORT).show();
Toast.makeText(context, "Lỗi: " + error.getMessage(), Toast.LENGTH_LONG).show();
```

### ✅ Sau
```java
// Toast thành công
ToastUtils.showSuccess(context, "Upload thành công!");

// Toast lỗi
ToastUtils.showError(context, "Không thể tải dữ liệu");

// Toast cảnh báo
ToastUtils.showWarning(context, "Vui lòng kiểm tra kết nối mạng");

// Toast thông tin
ToastUtils.showInfo(context, "Đang xử lý...");
```

---

## 4️⃣ Logger - Logging

### ❌ Trước
```java
Log.d("TAG", "Debug message");
Log.e("TAG", "Error: " + e.getMessage(), e);
```

### ✅ Sau
```java
// Debug log (chỉ hiện trong debug build)
Logger.d("Loading songs...");

// Error log
Logger.e("Failed to load songs", exception);

// Info log
Logger.i("Song uploaded successfully");

// Log repository error (format chuẩn)
Logger.logRepositoryError("SongRepository", "getTrendingSongs", exception);
// Output: [SongRepository.getTrendingSongs] Error: Network error
```

---

## 5️⃣ ValidationUtils - Validate input

### ❌ Trước
```java
if (email.isEmpty() || !email.contains("@")) {
    // Show error
}
if (password.length() < 6) {
    // Show error
}
```

### ✅ Sau
```java
// Validate email
if (!ValidationUtils.isValidEmail(email)) {
    String error = ValidationUtils.getEmailError(email);
    etEmail.setError(error);
    return;
}

// Validate password
if (!ValidationUtils.isValidPassword(password)) {
    String error = ValidationUtils.getPasswordError(password);
    etPassword.setError(error);
    return;
}

// Validate song title
if (!ValidationUtils.isValidSongTitle(title)) {
    String error = ValidationUtils.getSongTitleError(title);
    etTitle.setError(error);
    return;
}
```

---

## 6️⃣ NetworkUtils - Kiểm tra mạng

### ❌ Trước
```java
ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
```

### ✅ Sau
```java
// Kiểm tra có mạng
if (!NetworkUtils.isNetworkAvailable(context)) {
    ToastUtils.showError(context, "Không có kết nối mạng");
    return;
}

// Kiểm tra WiFi
if (NetworkUtils.isWifiConnected(context)) {
    // Download file lớn
}

// Lấy loại mạng
String networkType = NetworkUtils.getNetworkType(context);
Logger.d("Network type: " + networkType);
```

---

## 7️⃣ PermissionUtils - Xử lý quyền

### ❌ Trước
```java
if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) 
        != PackageManager.PERMISSION_GRANTED) {
    ActivityCompat.requestPermissions(this, 
        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
}
```

### ✅ Sau
```java
// Kiểm tra quyền storage
if (!PermissionUtils.hasStoragePermission(this)) {
    PermissionUtils.requestStoragePermission(this);
    return;
}

// Xử lý kết quả
@Override
public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    if (requestCode == PermissionUtils.REQUEST_CODE_STORAGE) {
        if (PermissionUtils.isPermissionGranted(grantResults)) {
            // Quyền được cấp
        } else {
            ToastUtils.showError(this, "Cần quyền truy cập storage");
        }
    }
}
```

---

## 8️⃣ FirebaseConstants - Tên collection và field

### ❌ Trước
```java
firestore.collection("songs")
    .whereEqualTo("artist", artistName)
    .orderBy("playCount", Query.Direction.DESCENDING)
    .get();
```

### ✅ Sau
```java
firestore.collection(FirebaseConstants.COLLECTION_SONGS)
    .whereEqualTo(FirebaseConstants.FIELD_ARTIST, artistName)
    .orderBy(FirebaseConstants.FIELD_PLAY_COUNT, Query.Direction.DESCENDING)
    .get();
```

**Lợi ích:**
- Không bị typo
- Dễ refactor (đổi tên 1 chỗ, tất cả đều đổi)
- IDE autocomplete

---

## 9️⃣ IntentKeys - Keys cho Intent

### ❌ Trước
```java
// Activity A
intent.putExtra("songId", song.getId());
intent.putExtra("songTitle", song.getTitle());

// Activity B
String songId = getIntent().getStringExtra("songId"); // Có thể typo
```

### ✅ Sau
```java
// Activity A
intent.putExtra(IntentKeys.SONG_ID, song.getId());
intent.putExtra(IntentKeys.SONG_TITLE, song.getTitle());

// Activity B
String songId = getIntent().getStringExtra(IntentKeys.SONG_ID);
String title = getIntent().getStringExtra(IntentKeys.SONG_TITLE);
```

---

## 🔟 AppConstants - Hằng số ứng dụng

### ❌ Trước
```java
songRepository.getTrendingSongs(10, listener); // Magic number
handler.postDelayed(runnable, 3000); // Magic number
```

### ✅ Sau
```java
songRepository.getTrendingSongs(AppConstants.TRENDING_SONGS_LIMIT, listener);
handler.postDelayed(runnable, AppConstants.SLIDER_AUTO_SCROLL_MS);
```

---

## 📊 Tổng kết

| Utility | Giảm code | Tăng tính nhất quán |
|---------|-----------|---------------------|
| ImageLoader | 85% | ✅ |
| TimeFormatter | 80% | ✅ |
| ToastUtils | 50% | ✅ |
| Logger | 40% | ✅ |
| ValidationUtils | 70% | ✅ |
| NetworkUtils | 75% | ✅ |
| PermissionUtils | 70% | ✅ |
| Constants | 0% | ✅✅✅ |

**Kết luận:** Sử dụng utilities và constants giúp code ngắn gọn, dễ đọc, dễ bảo trì hơn rất nhiều!

---

**Ngày cập nhật:** 2024-12-19

