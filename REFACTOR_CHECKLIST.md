# ✅ CHECKLIST REFACTOR CODE

## 📋 Tiến độ tổng thể

- [x] Tạo cấu trúc thư mục mới
- [x] Tạo các utility classes
- [x] Tạo các constants classes
- [x] Tạo các repository còn thiếu
- [ ] Refactor code để sử dụng utilities
- [ ] Refactor code để sử dụng constants
- [ ] Test lại toàn bộ ứng dụng

---

## 1️⃣ Utilities đã tạo

- [x] ImageLoader.java
- [x] TimeFormatter.java
- [x] Logger.java
- [x] ToastUtils.java
- [x] ValidationUtils.java
- [x] NetworkUtils.java
- [x] PermissionUtils.java

---

## 2️⃣ Constants đã tạo

- [x] FirebaseConstants.java
- [x] IntentKeys.java
- [x] AppConstants.java

---

## 3️⃣ Repositories đã tạo/hoàn thiện

- [x] SongRepository.java
- [x] AlbumRepository.java
- [x] SearchRepository.java
- [x] FavoriteRepository.java
- [x] SongUploadRepository.java
- [x] PlaylistRepository.java
- [x] HistoryRepository.java
- [x] AuthRepository.java
- [x] UserRepository.java
- [x] ProfileRepository.java

---

## 4️⃣ Adapters cần refactor

### AlbumAdapter.java
- [ ] Thay Glide code bằng ImageLoader
- [ ] Thay hardcoded strings bằng constants

### SongAdapter.java
- [ ] Thay Glide code bằng ImageLoader
- [ ] Thay format code bằng TimeFormatter

### SongListAdapter.java
- [ ] Thay Glide code bằng ImageLoader
- [ ] Thay formatPlayCount() bằng TimeFormatter.formatPlayCount()

### PlaylistAdapter.java
- [ ] Thay Glide code bằng ImageLoader

### GenreAdapter.java
- [ ] Thay Glide code bằng ImageLoader

### SliderAdapter.java
- [ ] Thay Glide code bằng ImageLoader

---

## 5️⃣ Fragments cần refactor

### HomeFragment.java
- [ ] Thay Glide code bằng ImageLoader
- [ ] Thay Toast bằng ToastUtils
- [ ] Thay Log bằng Logger
- [ ] Thay hardcoded limits bằng AppConstants

### SearchFragment.java
- [ ] Thay Toast bằng ToastUtils
- [ ] Thay Log bằng Logger
- [ ] Thay debounce time bằng AppConstants

### LibraryFragment.java
- [ ] Thay Glide code bằng ImageLoader
- [ ] Thay Toast bằng ToastUtils
- [ ] Thay Log bằng Logger

### ProfileFragment.java
- [ ] Thay Glide code bằng ImageLoader
- [ ] Thay Toast bằng ToastUtils
- [ ] Thay validation code bằng ValidationUtils

### MiniPlayerFragment.java
- [ ] Thay Glide code bằng ImageLoader
- [ ] Thay format time bằng TimeFormatter

---

## 6️⃣ Activities cần refactor

### PlayerActivity.java
- [ ] Thay Glide code bằng ImageLoader
- [ ] Thay Toast bằng ToastUtils
- [ ] Thay Log bằng Logger
- [ ] Thay format time bằng TimeFormatter
- [ ] Tách methods dài thành methods nhỏ

### MainActivity.java
- [ ] Thay Toast bằng ToastUtils

### LoginActivity.java
- [ ] Thay validation code bằng ValidationUtils
- [ ] Thay Toast bằng ToastUtils
- [ ] Thay Log bằng Logger

### RegisterActivity.java
- [ ] Thay validation code bằng ValidationUtils
- [ ] Thay Toast bằng ToastUtils
- [ ] Thay Log bằng Logger

### UploadSongActivity.java
- [ ] Thay permission code bằng PermissionUtils
- [ ] Thay validation code bằng ValidationUtils
- [ ] Thay Toast bằng ToastUtils
- [ ] Thay Log bằng Logger

### AlbumDetailActivity.java
- [ ] Thay Glide code bằng ImageLoader
- [ ] Thay Toast bằng ToastUtils
- [ ] Thay Intent extras bằng IntentKeys

### AllAlbumsActivity.java
- [ ] Thay Toast bằng ToastUtils
- [ ] Thay Intent extras bằng IntentKeys

### PlaylistDetailActivity.java
- [ ] Thay Glide code bằng ImageLoader
- [ ] Thay Toast bằng ToastUtils
- [ ] Thay Intent extras bằng IntentKeys

### AddSongPlaylistActivity.java
- [ ] Thay Toast bằng ToastUtils
- [ ] Thay Intent extras bằng IntentKeys

### GenreDetailActivity.java
- [ ] Thay Toast bằng ToastUtils
- [ ] Thay Intent extras bằng IntentKeys

---

## 7️⃣ Repositories cần refactor

### SongRepository.java
- [ ] Thay hardcoded collection names bằng FirebaseConstants
- [ ] Thay empty onError() bằng Logger

### AlbumRepository.java
- [ ] Thay hardcoded collection names bằng FirebaseConstants
- [ ] Thay Log.e bằng Logger

### PlaylistRepository.java
- [ ] Thay hardcoded collection names bằng FirebaseConstants
- [ ] Thay Log.e bằng Logger

### HistoryRepository.java
- [ ] Thay hardcoded collection names bằng FirebaseConstants

---

## 8️⃣ Testing

### Unit Tests
- [ ] Test ImageLoader
- [ ] Test TimeFormatter
- [ ] Test ValidationUtils
- [ ] Test NetworkUtils

### Integration Tests
- [ ] Test login flow
- [ ] Test upload flow
- [ ] Test player flow
- [ ] Test search flow

### UI Tests
- [ ] Test navigation
- [ ] Test player controls
- [ ] Test playlist management

---

## 9️⃣ Documentation

- [x] PROJECT_STRUCTURE.md
- [x] USAGE_GUIDE.md
- [x] REFACTOR_CHECKLIST.md
- [x] SETUP_COMPLETE.md
- [ ] README.md (cập nhật)

---

## 🎯 Ưu tiên

### High Priority (Làm ngay)
1. Refactor Adapters (dùng ImageLoader) - Giảm 85% code lặp
2. Refactor Repositories (dùng FirebaseConstants và Logger) - Tăng tính nhất quán
3. Refactor Activities (dùng ToastUtils, ValidationUtils) - Cải thiện UX

### Medium Priority (Làm sau)
4. Refactor Fragments (dùng utilities)
5. Thay Intent extras bằng IntentKeys

### Low Priority (Làm khi rảnh)
6. Viết tests
7. Cập nhật README

---

## 📊 Tiến độ

**Tổng số items:** ~60 items
**Đã hoàn thành:** ~25 items (42%)
**Còn lại:** ~35 items (58%)

---

## 💡 Gợi ý refactor nhanh

### Bước 1: Refactor 1 Adapter làm mẫu (30 phút)
Chọn `SongAdapter.java` để refactor hoàn chỉnh, sau đó áp dụng tương tự cho các adapter khác.

### Bước 2: Refactor tất cả Toast (1 giờ)
Tìm kiếm toàn bộ project: `Toast.makeText` và thay bằng `ToastUtils`

### Bước 3: Refactor tất cả Glide (2 giờ)
Tìm kiếm: `Glide.with` và thay bằng `ImageLoader`

### Bước 4: Refactor Firebase constants (1 giờ)
Tìm kiếm: `collection("songs")` và thay bằng `FirebaseConstants.COLLECTION_SONGS`

---

## 🔍 Lệnh tìm kiếm hữu ích

```bash
# Tìm tất cả Toast
grep -r "Toast.makeText" app/src/main/java/

# Tìm tất cả Glide
grep -r "Glide.with" app/src/main/java/

# Tìm hardcoded collection names
grep -r 'collection("' app/src/main/java/

# Tìm hardcoded Intent keys
grep -r 'putExtra("' app/src/main/java/
```

---

**Cập nhật lần cuối:** 2024-12-19
**Tiến độ:** 42% hoàn thành

