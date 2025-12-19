# 🔧 HƯỚNG DẪN TỰ REFACTOR - TỪNG BƯỚC CHI TIẾT

> **Mục tiêu:** Giúp bạn tự refactor code để sử dụng các Utility classes đã tạo
> 
> **Thời gian ước tính:** 3-4 giờ cho toàn bộ project
> 
> **Kỹ năng cần:** Biết Find & Replace trong Android Studio

---

## 📋 MỤC LỤC

1. [Chuẩn bị](#1-chuẩn-bị)
2. [Refactor Toast (57 chỗ)](#2-refactor-toast-57-chỗ)
3. [Refactor Glide (19 chỗ)](#3-refactor-glide-19-chỗ)
4. [Refactor Log (25 chỗ)](#4-refactor-log-25-chỗ)
5. [Refactor Validation](#5-refactor-validation)
6. [Refactor TimeFormatter](#6-refactor-timeformatter)
7. [Kiểm tra và Test](#7-kiểm-tra-và-test)

---

## 1️⃣ CHUẨN BỊ

### Bước 1.1: Sync Project
```
File → Sync Project with Gradle Files
```

### Bước 1.2: Tạo Git Commit (QUAN TRỌNG!)
```bash
git add .
git commit -m "Before refactoring - backup"
```

**Lý do:** Nếu refactor sai, bạn có thể quay lại!

### Bước 1.3: Mở Find & Replace
```
Ctrl + Shift + R (Windows/Linux)
Cmd + Shift + R (Mac)
```

---

## 2️⃣ REFACTOR TOAST (57 chỗ)

### 📊 Thống kê
- **Tổng số:** 57 lần `Toast.makeText`
- **Thời gian:** ~30 phút
- **Độ khó:** ⭐ Dễ

### Bước 2.1: Thêm import vào từng file

**Tìm kiếm trong:** `app/src/main/java/com/example/musicapplication/ui`

**Tìm:** `Toast.makeText`

**Kết quả:** Sẽ thấy danh sách các file sử dụng Toast

**Hành động:** Mở từng file và thêm import:
```java
import com.example.musicapplication.utils.ToastUtils;
```

### Bước 2.2: Replace từng pattern

#### Pattern 1: Toast thành công
**TÌM:**
```java
Toast.makeText(this, "Upload thành công", Toast.LENGTH_SHORT).show();
```

**THAY BẰNG:**
```java
ToastUtils.showSuccess(this, "Upload thành công");
```

#### Pattern 2: Toast lỗi
**TÌM:**
```java
Toast.makeText(this, "Lỗi: " + error.getMessage(), Toast.LENGTH_LONG).show();
```

**THAY BẰNG:**
```java
ToastUtils.showError(this, error.getMessage());
```

#### Pattern 3: Toast thông thường
**TÌM:**
```java
Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
```

**THAY BẰNG:**
```java
ToastUtils.showShort(context, message);
```

### Bước 2.3: Quy tắc chọn loại Toast

| Nội dung message | Dùng method |
|------------------|-------------|
| "thành công", "hoàn thành", "đã lưu" | `showSuccess()` |
| "lỗi", "thất bại", "không thể" | `showError()` |
| "cảnh báo", "vui lòng", "kiểm tra" | `showWarning()` |
| Các trường hợp khác | `showInfo()` hoặc `showShort()` |

### Bước 2.4: Ví dụ cụ thể

**File: LoginActivity.java**

❌ **TRƯỚC:**
```java
Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
Toast.makeText(this, "Email hoặc mật khẩu không đúng", Toast.LENGTH_LONG).show();
```

✅ **SAU:**
```java
ToastUtils.showSuccess(this, "Đăng nhập thành công!");
ToastUtils.showError(this, "Email hoặc mật khẩu không đúng");
```

---

## 3️⃣ REFACTOR GLIDE (19 chỗ)

### 📊 Thống kê
- **Tổng số:** 19 lần `Glide.with`
- **Thời gian:** ~45 phút
- **Độ khó:** ⭐⭐ Trung bình

### Bước 3.1: Thêm import

**Thêm vào từng file:**
```java
import com.example.musicapplication.utils.ImageLoader;
```

**XÓA import không cần thiết:**
```java
// XÓA các dòng này nếu không dùng Glide trực tiếp nữa
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
```

### Bước 3.2: Replace từng pattern

#### Pattern 1: Load ảnh cơ bản
**TÌM:**
```java
Glide.with(context)
    .load(url)
    .placeholder(R.drawable.ic_music)
    .error(R.drawable.ic_music)
    .centerCrop()
    .into(imageView);
```

**THAY BẰNG:**
```java
ImageLoader.load(context, url, imageView);
```

#### Pattern 2: Load ảnh bo góc tròn
**TÌM:**
```java
Glide.with(context)
    .load(url)
    .placeholder(R.drawable.ic_music)
    .error(R.drawable.ic_music)
    .transform(new CenterCrop(), new RoundedCorners(16))
    .into(imageView);
```

**THAY BẰNG:**
```java
ImageLoader.loadRounded(context, url, imageView, 16);
```

#### Pattern 3: Load ảnh tròn (avatar)
**TÌM:**
```java
Glide.with(context)
    .load(url)
    .placeholder(R.drawable.ic_music)
    .error(R.drawable.ic_music)
    .circleCrop()
    .into(imageView);
```

**THAY BẰNG:**
```java
ImageLoader.loadCircle(context, url, imageView);
```

### Bước 3.3: Ví dụ cụ thể

**File: SongAdapter.java**

❌ **TRƯỚC:**
```java
Glide.with(context)
    .load(song.getImageUrl())
    .placeholder(R.drawable.ic_music)
    .error(R.drawable.ic_music)
    .centerCrop()
    .diskCacheStrategy(DiskCacheStrategy.ALL)
    .into(holder.image);
```

✅ **SAU:**
```java
ImageLoader.load(context, song.getImageUrl(), holder.image);
```

**Giảm từ 7 dòng xuống 1 dòng!** 🎉

---

## 4️⃣ REFACTOR LOG (25 chỗ)

### 📊 Thống kê
- **Tổng số:** 25 lần `Log.d` / `Log.e`
- **Thời gian:** ~20 phút
- **Độ khó:** ⭐ Dễ

### Bước 4.1: Thêm import

```java
import com.example.musicapplication.utils.Logger;
```

### Bước 4.2: Replace patterns

#### Pattern 1: Log.d
**TÌM:**
```java
Log.d(TAG, "Loading songs...");
```

**THAY BẰNG:**
```java
Logger.d("Loading songs...");
```

#### Pattern 2: Log.e
**TÌM:**
```java
Log.e(TAG, "Error: " + e.getMessage(), e);
```

**THAY BẰNG:**
```java
Logger.e("Error: " + e.getMessage(), e);
```

### Bước 4.3: Xóa TAG constants

**SAU KHI** refactor xong tất cả Log, bạn có thể xóa:
```java
private static final String TAG = "ActivityName";
```

**Lý do:** Logger tự động tạo TAG từ tên class!

---

## 5️⃣ REFACTOR VALIDATION

### 📊 Thống kê
- **Files cần refactor:** LoginActivity, RegisterActivity, UploadSongActivity, ProfileFragment
- **Thời gian:** ~30 phút
- **Độ khó:** ⭐⭐ Trung bình

### Bước 5.1: Thêm import

```java
import com.example.musicapplication.utils.ValidationUtils;
```

### Bước 5.2: Refactor Email Validation

**File: LoginActivity.java, RegisterActivity.java**

❌ **TRƯỚC:**
```java
String email = etEmail.getText().toString().trim();
if (email.isEmpty()) {
    etEmail.setError("Email không được để trống");
    return;
}
if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
    etEmail.setError("Email không hợp lệ");
    return;
}
```

✅ **SAU:**
```java
String email = etEmail.getText().toString().trim();
if (!ValidationUtils.isValidEmail(email)) {
    etEmail.setError(ValidationUtils.getEmailError(email));
    return;
}
```

**Giảm từ 8 dòng xuống 4 dòng!**

### Bước 5.3: Refactor Password Validation

❌ **TRƯỚC:**
```java
String password = etPassword.getText().toString();
if (password.isEmpty()) {
    etPassword.setError("Mật khẩu không được để trống");
    return;
}
if (password.length() < 6) {
    etPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
    return;
}
```

✅ **SAU:**
```java
String password = etPassword.getText().toString();
if (!ValidationUtils.isValidPassword(password)) {
    etPassword.setError(ValidationUtils.getPasswordError(password));
    return;
}
```

### Bước 5.4: Refactor Song Title Validation

**File: UploadSongActivity.java**

❌ **TRƯỚC:**
```java
String title = etTitle.getText().toString().trim();
if (title.isEmpty()) {
    etTitle.setError("Tên bài hát không được để trống");
    return;
}
if (title.length() > 100) {
    etTitle.setError("Tên bài hát quá dài");
    return;
}
```

✅ **SAU:**
```java
String title = etTitle.getText().toString().trim();
if (!ValidationUtils.isValidSongTitle(title)) {
    etTitle.setError(ValidationUtils.getSongTitleError(title));
    return;
}
```

---

## 6️⃣ REFACTOR TIMEFORMATTER

### 📊 Thống kê
- **Files cần refactor:** PlayerActivity, MiniPlayerFragment, SongAdapter
- **Thời gian:** ~15 phút
- **Độ khó:** ⭐ Dễ

### Bước 6.1: Thêm import

```java
import com.example.musicapplication.utils.TimeFormatter;
```

### Bước 6.2: Refactor Duration Format

**File: PlayerActivity.java, MiniPlayerFragment.java**

❌ **TRƯỚC:**
```java
private String formatDuration(long milliseconds) {
    long seconds = milliseconds / 1000;
    long minutes = seconds / 60;
    seconds = seconds % 60;
    return String.format(Locale.US, "%02d:%02d", minutes, seconds);
}

// Sử dụng
tvDuration.setText(formatDuration(song.getDuration()));
```

✅ **SAU:**
```java
// XÓA method formatDuration()

// Sử dụng trực tiếp
tvDuration.setText(TimeFormatter.formatDuration(song.getDuration()));
```

### Bước 6.3: Refactor Play Count Format

**File: SongAdapter.java, HomeFragment.java**

❌ **TRƯỚC:**
```java
private String formatPlayCount(long count) {
    if (count >= 1000000) {
        return String.format(Locale.US, "%.1fM", count / 1000000.0);
    } else if (count >= 1000) {
        return String.format(Locale.US, "%.1fK", count / 1000.0);
    }
    return String.valueOf(count);
}
```

✅ **SAU:**
```java
// XÓA method formatPlayCount()

// Sử dụng
tvPlayCount.setText(TimeFormatter.formatPlayCount(song.getPlayCount()));
```

### Bước 6.4: Refactor Time Ago

❌ **TRƯỚC:**
```java
// Code phức tạp tính "2 giờ trước", "3 ngày trước"...
```

✅ **SAU:**
```java
tvTime.setText(TimeFormatter.formatTimeAgo(song.getCreatedAt()));
```

---

## 7️⃣ KIỂM TRA VÀ TEST

### Bước 7.1: Build Project

```
Build → Clean Project
Build → Rebuild Project
```

**Kiểm tra:** Không có lỗi compile

### Bước 7.2: Chạy App

```
Run → Run 'app'
```

**Kiểm tra:**
- ✅ App chạy không crash
- ✅ Toast hiển thị đúng với emoji
- ✅ Ảnh load đúng
- ✅ Validation hoạt động
- ✅ Thời gian format đúng

### Bước 7.3: Test từng chức năng

| Chức năng | Cách test | Kết quả mong đợi |
|-----------|-----------|------------------|
| **Toast** | Đăng nhập sai | Hiện "❌ Email hoặc mật khẩu không đúng" |
| **ImageLoader** | Xem danh sách bài hát | Ảnh album hiển thị đúng |
| **Validation** | Nhập email sai | Hiện lỗi "Email không hợp lệ" |
| **TimeFormatter** | Xem player | Thời gian hiện "03:45" |

### Bước 7.4: Commit Changes

```bash
git add .
git commit -m "Refactor: Use utility classes (Toast, Glide, Log, Validation, TimeFormatter)"
```

---

## 📊 CHECKLIST REFACTOR

### Activities

- [ ] **LoginActivity.java**
  - [ ] Toast → ToastUtils
  - [ ] Validation → ValidationUtils
  - [ ] Log → Logger

- [ ] **RegisterActivity.java**
  - [ ] Toast → ToastUtils
  - [ ] Validation → ValidationUtils
  - [ ] Log → Logger

- [ ] **PlayerActivity.java**
  - [ ] Toast → ToastUtils
  - [ ] Glide → ImageLoader
  - [ ] Log → Logger
  - [ ] formatDuration → TimeFormatter

- [ ] **UploadSongActivity.java**
  - [ ] Toast → ToastUtils
  - [ ] Validation → ValidationUtils
  - [ ] Log → Logger

- [ ] **AlbumDetailActivity.java**
  - [ ] Toast → ToastUtils
  - [ ] Glide → ImageLoader

- [ ] **PlaylistDetailActivity.java**
  - [ ] Toast → ToastUtils
  - [ ] Glide → ImageLoader

- [ ] **MainActivity.java**
  - [ ] Toast → ToastUtils

### Fragments

- [ ] **HomeFragment.java**
  - [ ] Toast → ToastUtils
  - [ ] Glide → ImageLoader
  - [ ] Log → Logger

- [ ] **SearchFragment.java**
  - [ ] Toast → ToastUtils
  - [ ] Log → Logger

- [ ] **LibraryFragment.java**
  - [ ] Toast → ToastUtils
  - [ ] Glide → ImageLoader

- [ ] **ProfileFragment.java**
  - [ ] Toast → ToastUtils
  - [ ] Glide → ImageLoader
  - [ ] Validation → ValidationUtils

- [ ] **MiniPlayerFragment.java**
  - [ ] Glide → ImageLoader
  - [ ] formatDuration → TimeFormatter

### Adapters

- [ ] **SongAdapter.java**
  - [ ] Glide → ImageLoader
  - [ ] formatDuration → TimeFormatter

- [ ] **AlbumAdapter.java**
  - [ ] Glide → ImageLoader

- [ ] **PlaylistAdapter.java**
  - [ ] Glide → ImageLoader

- [ ] **SliderAdapter.java**
  - [ ] Glide → ImageLoader

---

## 💡 MẸO REFACTOR NHANH

### Mẹo 1: Dùng Find & Replace toàn project

**Bước 1:** Mở Find & Replace
```
Ctrl + Shift + R
```

**Bước 2:** Chọn scope
```
Scope: Project Files
File mask: *.java
Directory: app/src/main/java/com/example/musicapplication/ui
```

**Bước 3:** Tìm và thay từng pattern

**Ví dụ:** Thay tất cả Toast thành công
```
Find: Toast.makeText\(([^,]+), "([^"]*thành công[^"]*)", Toast.LENGTH_SHORT\).show\(\);
Replace: ToastUtils.showSuccess($1, "$2");
☑ Regex
```

### Mẹo 2: Refactor từng file một

**Ưu điểm:**
- Kiểm soát tốt hơn
- Dễ test từng file
- Ít rủi ro hơn

**Nhược điểm:**
- Mất nhiều thời gian hơn

**Khuyến nghị:** Dùng cách này nếu bạn mới refactor lần đầu!

### Mẹo 3: Refactor theo thứ tự ưu tiên

**Thứ tự khuyến nghị:**
1. **LoginActivity, RegisterActivity** (quan trọng nhất)
2. **PlayerActivity** (dùng nhiều nhất)
3. **SongAdapter, AlbumAdapter** (xuất hiện nhiều)
4. **Fragments** (HomeFragment, SearchFragment...)
5. **Các Activity khác**

---

## ⚠️ LƯU Ý QUAN TRỌNG

### ❌ KHÔNG NÊN

1. **Refactor tất cả cùng lúc** - Dễ gây lỗi khó debug
2. **Quên commit trước khi refactor** - Không thể rollback nếu sai
3. **Không test sau khi refactor** - Có thể có lỗi runtime
4. **Copy-paste mù quáng** - Hiểu code trước khi thay

### ✅ NÊN

1. **Refactor từng file một** - Dễ kiểm soát
2. **Commit sau mỗi file** - Dễ rollback nếu cần
3. **Test ngay sau khi refactor** - Phát hiện lỗi sớm
4. **Đọc kỹ code cũ** - Hiểu logic trước khi thay

---

## 🆘 GẶP VẤN ĐỀ?

### Lỗi: Cannot resolve symbol 'ToastUtils'

**Nguyên nhân:** Chưa import hoặc chưa sync project

**Giải pháp:**
```java
// Thêm import
import com.example.musicapplication.utils.ToastUtils;

// Sync project
File → Sync Project with Gradle Files
```

### Lỗi: App crash khi hiển thị Toast

**Nguyên nhân:** Context bị null

**Giải pháp:**
```java
// Kiểm tra context trước khi dùng
if (getContext() != null) {
    ToastUtils.showSuccess(getContext(), "Success");
}
```

### Lỗi: Ảnh không hiển thị sau khi refactor

**Nguyên nhân:** URL null hoặc ImageView null

**Giải pháp:**
```java
// ImageLoader đã tự động check null, nhưng nên kiểm tra URL
if (song.getImageUrl() != null && !song.getImageUrl().isEmpty()) {
    ImageLoader.load(context, song.getImageUrl(), imageView);
}
```

---

## 📈 THEO DÕI TIẾN ĐỘ

| Loại | Tổng số | Đã refactor | Còn lại | % Hoàn thành |
|------|---------|-------------|---------|--------------|
| Toast | 57 | 0 | 57 | 0% |
| Glide | 19 | 0 | 19 | 0% |
| Log | 25 | 0 | 25 | 0% |
| Validation | ~10 | 0 | ~10 | 0% |
| TimeFormatter | ~5 | 0 | ~5 | 0% |
| **TỔNG** | **~116** | **0** | **~116** | **0%** |

**Cập nhật bảng này sau mỗi lần refactor để theo dõi tiến độ!**

---

## 🎯 MỤC TIÊU CUỐI CÙNG

Sau khi refactor xong, code của bạn sẽ:

✅ **Ngắn gọn hơn** - Giảm ~30-40% số dòng code

✅ **Dễ đọc hơn** - Tên method rõ ràng, dễ hiểu

✅ **Dễ bảo trì hơn** - Thay đổi 1 chỗ, áp dụng toàn project

✅ **Nhất quán hơn** - Tất cả Toast/Glide/Log đều giống nhau

✅ **Chuyên nghiệp hơn** - Tuân thủ best practices

---

**🎉 CHÚC BẠN REFACTOR THÀNH CÔNG!**

**Nếu gặp khó khăn, hãy hỏi tôi!** 💪


