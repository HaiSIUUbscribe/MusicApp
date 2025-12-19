# 📝 REFACTOR QUICK REFERENCE - TRA CỨU NHANH

> **Mục đích:** Tra cứu nhanh cách thay thế code cũ bằng utility classes
> 
> **Sử dụng:** Mở file này khi refactor để copy-paste nhanh

---

## 🔍 TÌM KIẾM NHANH

**Nhấn Ctrl+F và tìm:**
- `Toast` - Xem cách thay Toast
- `Glide` - Xem cách thay Glide
- `Log` - Xem cách thay Log
- `Validation` - Xem cách validate
- `Time` - Xem cách format thời gian

---

## 1️⃣ TOAST - ToastUtils

### Import
```java
import com.example.musicapplication.utils.ToastUtils;
```

### Thay thế

| ❌ CŨ | ✅ MỚI |
|-------|--------|
| `Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();` | `ToastUtils.showSuccess(this, "Success");` |
| `Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();` | `ToastUtils.showError(this, "Error");` |
| `Toast.makeText(this, "Warning", Toast.LENGTH_LONG).show();` | `ToastUtils.showWarning(this, "Warning");` |
| `Toast.makeText(this, "Info", Toast.LENGTH_SHORT).show();` | `ToastUtils.showInfo(this, "Info");` |
| `Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();` | `ToastUtils.showShort(this, msg);` |
| `Toast.makeText(this, msg, Toast.LENGTH_LONG).show();` | `ToastUtils.showLong(this, msg);` |

### Ví dụ cụ thể

```java
// ❌ CŨ
Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
Toast.makeText(this, "Lỗi: " + error.getMessage(), Toast.LENGTH_LONG).show();
Toast.makeText(getContext(), "Vui lòng kiểm tra kết nối", Toast.LENGTH_LONG).show();

// ✅ MỚI
ToastUtils.showSuccess(this, "Đăng nhập thành công!");
ToastUtils.showError(this, error.getMessage());
ToastUtils.showWarning(getContext(), "Vui lòng kiểm tra kết nối");
```

---

## 2️⃣ GLIDE - ImageLoader

### Import
```java
import com.example.musicapplication.utils.ImageLoader;
```

### Thay thế

#### A. Load ảnh cơ bản

```java
// ❌ CŨ
Glide.with(context)
    .load(url)
    .placeholder(R.drawable.ic_music)
    .error(R.drawable.ic_music)
    .centerCrop()
    .into(imageView);

// ✅ MỚI
ImageLoader.load(context, url, imageView);
```

#### B. Load ảnh bo góc tròn

```java
// ❌ CŨ
Glide.with(context)
    .load(url)
    .placeholder(R.drawable.ic_music)
    .error(R.drawable.ic_music)
    .transform(new CenterCrop(), new RoundedCorners(16))
    .into(imageView);

// ✅ MỚI
ImageLoader.loadRounded(context, url, imageView, 16);
```

#### C. Load ảnh tròn (avatar)

```java
// ❌ CŨ
Glide.with(context)
    .load(url)
    .placeholder(R.drawable.ic_music)
    .error(R.drawable.ic_music)
    .circleCrop()
    .into(imageView);

// ✅ MỚI
ImageLoader.loadCircle(context, url, imageView);
```

#### D. Load ảnh với callback

```java
// ❌ CŨ
Glide.with(context)
    .asBitmap()
    .load(url)
    .into(new CustomTarget<Bitmap>() {
        @Override
        public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
            // Xử lý bitmap
        }
        @Override
        public void onLoadCleared(Drawable placeholder) {}
    });

// ✅ MỚI
ImageLoader.loadWithCallback(context, url, new ImageLoader.OnImageLoadedListener() {
    @Override
    public void onImageLoaded(Bitmap bitmap) {
        // Xử lý bitmap
    }
});
```

### Ví dụ trong Adapter

```java
// ❌ CŨ - SongAdapter.java
Glide.with(context)
    .load(song.getImageUrl())
    .placeholder(R.drawable.ic_music)
    .error(R.drawable.ic_music)
    .centerCrop()
    .diskCacheStrategy(DiskCacheStrategy.ALL)
    .into(holder.image);

// ✅ MỚI
ImageLoader.load(context, song.getImageUrl(), holder.image);
```

---

## 3️⃣ LOG - Logger

### Import
```java
import com.example.musicapplication.utils.Logger;
```

### Thay thế

| ❌ CŨ | ✅ MỚI |
|-------|--------|
| `Log.d(TAG, "Debug message");` | `Logger.d("Debug message");` |
| `Log.e(TAG, "Error message");` | `Logger.e("Error message");` |
| `Log.e(TAG, "Error", exception);` | `Logger.e("Error", exception);` |
| `Log.i(TAG, "Info message");` | `Logger.i("Info message");` |
| `Log.w(TAG, "Warning message");` | `Logger.w("Warning message");` |

### Xóa TAG constant

```java
// ❌ CŨ - Có thể XÓA sau khi refactor
private static final String TAG = "LoginActivity";

// ✅ MỚI - Không cần TAG nữa!
// Logger tự động tạo TAG từ tên class
```

### Ví dụ

```java
// ❌ CŨ
private static final String TAG = "PlayerActivity";
Log.d(TAG, "Loading song: " + song.getTitle());
Log.e(TAG, "Error loading song", exception);

// ✅ MỚI
Logger.d("Loading song: " + song.getTitle());
Logger.e("Error loading song", exception);
```

---

## 4️⃣ VALIDATION - ValidationUtils

### Import
```java
import com.example.musicapplication.utils.ValidationUtils;
```

### A. Email Validation

```java
// ❌ CŨ
String email = etEmail.getText().toString().trim();
if (email.isEmpty()) {
    etEmail.setError("Email không được để trống");
    return;
}
if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
    etEmail.setError("Email không hợp lệ");
    return;
}

// ✅ MỚI
String email = etEmail.getText().toString().trim();
if (!ValidationUtils.isValidEmail(email)) {
    etEmail.setError(ValidationUtils.getEmailError(email));
    return;
}
```

### B. Password Validation

```java
// ❌ CŨ
String password = etPassword.getText().toString();
if (password.isEmpty()) {
    etPassword.setError("Mật khẩu không được để trống");
    return;
}
if (password.length() < 6) {
    etPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
    return;
}

// ✅ MỚI
String password = etPassword.getText().toString();
if (!ValidationUtils.isValidPassword(password)) {
    etPassword.setError(ValidationUtils.getPasswordError(password));
    return;
}
```

### C. Song Title Validation

```java
// ❌ CŨ
String title = etTitle.getText().toString().trim();
if (title.isEmpty()) {
    etTitle.setError("Tên bài hát không được để trống");
    return;
}
if (title.length() > 100) {
    etTitle.setError("Tên bài hát quá dài");
    return;
}

// ✅ MỚI
String title = etTitle.getText().toString().trim();
if (!ValidationUtils.isValidSongTitle(title)) {
    etTitle.setError(ValidationUtils.getSongTitleError(title));
    return;
}
```

### D. Playlist Name Validation

```java
// ❌ CŨ
String name = etPlaylistName.getText().toString().trim();
if (name.isEmpty() || name.length() > 50) {
    etPlaylistName.setError("Tên playlist không hợp lệ");
    return;
}

// ✅ MỚI
String name = etPlaylistName.getText().toString().trim();
if (!ValidationUtils.isValidPlaylistName(name)) {
    etPlaylistName.setError(ValidationUtils.getPlaylistNameError(name));
    return;
}
```

---

## 5️⃣ TIME FORMATTER - TimeFormatter

### Import
```java
import com.example.musicapplication.utils.TimeFormatter;
```

### A. Format Duration (mm:ss)

```java
// ❌ CŨ
private String formatDuration(long milliseconds) {
    long seconds = milliseconds / 1000;
    long minutes = seconds / 60;
    seconds = seconds % 60;
    return String.format(Locale.US, "%02d:%02d", minutes, seconds);
}

tvDuration.setText(formatDuration(song.getDuration()));

// ✅ MỚI
// XÓA method formatDuration()
tvDuration.setText(TimeFormatter.formatDuration(song.getDuration()));
```

### B. Format Play Count (1.2K, 3.5M)

```java
// ❌ CŨ
private String formatPlayCount(long count) {
    if (count >= 1000000) {
        return String.format(Locale.US, "%.1fM", count / 1000000.0);
    } else if (count >= 1000) {
        return String.format(Locale.US, "%.1fK", count / 1000.0);
    }
    return String.valueOf(count);
}

tvPlayCount.setText(formatPlayCount(song.getPlayCount()));

// ✅ MỚI
// XÓA method formatPlayCount()
tvPlayCount.setText(TimeFormatter.formatPlayCount(song.getPlayCount()));
```

### C. Format Time Ago

```java
// ❌ CŨ
// Code phức tạp tính khoảng thời gian...

// ✅ MỚI
tvTime.setText(TimeFormatter.formatTimeAgo(song.getCreatedAt()));
```

---

## 6️⃣ NETWORK - NetworkUtils

### Import
```java
import com.example.musicapplication.utils.NetworkUtils;
```

### Thay thế

```java
// ❌ CŨ
private boolean isNetworkAvailable() {
    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
    return activeNetwork != null && activeNetwork.isConnected();
}

if (!isNetworkAvailable()) {
    Toast.makeText(this, "Không có kết nối mạng", Toast.LENGTH_SHORT).show();
    return;
}

// ✅ MỚI
// XÓA method isNetworkAvailable()

if (!NetworkUtils.isNetworkAvailable(this)) {
    ToastUtils.showError(this, "Không có kết nối mạng");
    return;
}
```

---

## 7️⃣ CONSTANTS - AppConstants, FirebaseConstants

### Import
```java
import com.example.musicapplication.constants.AppConstants;
import com.example.musicapplication.constants.FirebaseConstants;
import com.example.musicapplication.constants.IntentKeys;
```

### Thay thế

```java
// ❌ CŨ - Magic numbers
songRepository.getTrendingSongs(10, listener);
handler.postDelayed(runnable, 3000);

// ✅ MỚI
songRepository.getTrendingSongs(AppConstants.TRENDING_SONGS_LIMIT, listener);
handler.postDelayed(runnable, AppConstants.SLIDER_AUTO_SCROLL_MS);
```

---

## 📋 CHECKLIST NHANH

Khi refactor 1 file, kiểm tra:

- [ ] Đã thêm imports cần thiết?
- [ ] Đã thay tất cả Toast?
- [ ] Đã thay tất cả Glide?
- [ ] Đã thay tất cả Log?
- [ ] Đã thay validation code?
- [ ] Đã thay format time code?
- [ ] Đã xóa methods không dùng nữa?
- [ ] Đã xóa imports không dùng?
- [ ] Đã test chạy thử?
- [ ] Đã commit changes?

---

**💡 MẸO:** Bookmark file này trong Android Studio để tra cứu nhanh!

**Cách bookmark:** Click chuột phải vào file → Add to Favorites


