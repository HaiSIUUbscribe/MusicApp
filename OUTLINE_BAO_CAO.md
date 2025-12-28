# OUTLINE BÁO CÁO DỰ ÁN - PHIÊN BẢN SINH VIÊN ĐẠI HỌC

## **Thông tin chung**
- **Tiêu đề**: Ứng dụng Nghe Nhạc trên Android với Firebase
- **Tổng số trang**: 25-30 trang
- **Định dạng**: Báo cáo đồ án môn học/Đồ án cuối kỳ

---

## **MỤC LỤC**

### **PHẦN MỞ ĐẦU** (3 trang)

#### **Trang bìa** (1 trang)
- Tên đề tài, họ tên, lớp, MSSV
- Giảng viên hướng dẫn
- Thời gian thực hiện

#### **Mục lục** (1 trang)

#### **Lời mở đầu** (1 trang)
- Lý do chọn đề tài
- Mục tiêu
- Kết quả đạt được

---

### **CHƯƠNG 1: TỔNG QUAN ĐỀ TÀI** (3-4 trang)

#### **1.1. Giới thiệu** (1 trang)
- Xu hướng nghe nhạc trực tuyến
- Nhu cầu ứng dụng music player

#### **1.2. Mục tiêu** (0.5 trang)
- Xây dựng app nghe nhạc đầy đủ tính năng
- Áp dụng Firebase backend
- Code sạch, dễ bảo trì

#### **1.3. Tính năng chính** (1 trang)
- **Người dùng**: Đăng ký, đăng nhập, quản lý profile
- **Nghe nhạc**: Play/pause, next/previous, shuffle, repeat, điều chỉnh âm lượng
- **Quản lý**: Playlist, yêu thích, lịch sử
- **Khác**: Tìm kiếm, upload bài hát

#### **1.4. Công nghệ sử dụng** (0.5 trang)
- **Platform**: Android (Java)
- **Backend**: Firebase (Authentication, Firestore, Storage)
- **Libraries**: Glide, Material Design
- **Tools**: Android Studio, Git

#### **1.5. Phạm vi** (0.5 trang)
- Android 8.0+ (API 27+)
- Kết nối internet bắt buộc
- Single user session

---

### **CHƯƠNG 2: CƠ SỞ LÝ THUYẾT** (3-4 trang)

#### **2.1. Android cơ bản** (1.5 trang)
- **Activity & Fragment**: Lifecycle, navigation
- **RecyclerView**: Hiển thị danh sách
- **ViewBinding**: Truy cập view
- **Material Design**: UI components

#### **2.2. Firebase** (1.5 trang)
- **Authentication**: Đăng ký/đăng nhập
- **Firestore**: Database NoSQL, real-time
- **Storage**: Lưu trữ file audio/image

#### **2.3. Design Patterns** (1 trang)
- **Repository Pattern**: Tách data layer
- **Singleton Pattern**: MusicPlayer instance
- **Handler Pattern**: Tách logic UI

---

### **CHƯƠNG 3: THIẾT KẾ HỆ THỐNG** (4-5 trang)

#### **3.1. Kiến trúc ứng dụng** (1.5 trang)
- **3 lớp chính**:
  - **UI Layer**: Activities, Fragments, Adapters
  - **Data Layer**: Repositories, Firebase services
  - **Business Logic**: MusicPlayer, PlaylistManager
- **Sơ đồ kiến trúc** (diagram)

#### **3.2. Cấu trúc thư mục** (1 trang)
```
app/src/main/java/com/example/musicapplication/
├── ui/           # Giao diện
├── data/         # Dữ liệu (repositories)
├── model/        # Data models
├── player/       # Logic phát nhạc
├── utils/        # Utilities
└── constants/    # Hằng số
```

#### **3.3. Cơ sở dữ liệu Firestore** (1.5 trang)
- **Collections**:
  - `users`: Thông tin user
  - `songs`: Bài hát (title, artist, audioUrl, imageUrl, playCount...)
  - `albums`: Album nhạc
  - `playlists`: Playlist của user
  - `history`: Lịch sử nghe
- **Composite Index**: artist + playCount (cho sắp xếp)

#### **3.4. Thiết kế màn hình** (1 trang)
- **Luồng điều hướng**:
  - Login/Register → MainActivity (Bottom nav)
  - Home / Library / Search / Profile (4 tabs)
  - Mini Player (bottom) → Full Player (activity)
- **Wireframes** (hình minh họa các màn hình chính)

---

### **CHƯƠNG 4: TRIỂN KHAI** (8-10 trang)

#### **4.1. Cấu hình dự án** (1 trang)
- **build.gradle**:
  - compileSdk 36, minSdk 27
  - Firebase BOM, Glide
  - ViewBinding enabled
- **Firebase setup**: google-services.json

#### **4.2. Data Layer** (2.5 trang)

**4.2.1. Models** (0.5 trang)
```java
Song, Album, Playlist, User, History
```

**4.2.2. Repositories** (2 trang)
- **SongRepository**:
  ```java
  getTrendingSongs(limit, callback)
  getSongsByArtist(artist, callback)
  searchSongs(query, callback)
  ```
  - Network check trước query
  - Error handling: "Không có kết nối mạng"
  
- **PlaylistRepository**: CRUD playlists
- **FavoriteRepository**: Toggle like/unlike
- **HistoryRepository**: Lưu lịch sử
- **AuthRepository**: Login/Register

**Code mẫu** (Repository với network check):
```java
if (!NetworkUtils.isNetworkAvailable(context)) {
    callback.onError("Không có kết nối mạng");
    return;
}
firestore.collection("songs")
    .orderBy("playCount", Query.Direction.DESCENDING)
    .limit(limit)
    .get()
    .addOnSuccessListener(...)
    .addOnFailureListener(...);
```

#### **4.3. Business Logic** (1.5 trang)

**4.3.1. MusicPlayer** (1 trang)
```java
public class MusicPlayer {
    private static MusicPlayer instance;
    private MediaPlayer mediaPlayer;
    
    public void play(String audioUrl)
    public void pause()
    public void next()
    public void previous()
    public void seekTo(int position)
}
```

**4.3.2. Utility Classes** (0.5 trang)
- **ImageLoader**: Glide wrapper
- **TimeFormatter**: Format duration (mm:ss)
- **ToastUtils**: Hiển thị thông báo
- **NetworkUtils**: Kiểm tra mạng

#### **4.4. UI Layer** (4 trang)

**4.4.1. MainActivity** (0.5 trang)
- BottomNavigationView với 4 tabs
- Fragment container
- Mini player fragment

**4.4.2. Fragments** (1.5 trang)

- **HomeFragment**:
  - Slider (banner)
  - Popular Albums (RecyclerView)
  - Trending Artists
  - New Songs
  - **Handler Pattern**:
    ```java
    HomeAlbumsHandler
    HomeArtistsHandler
    HomePopularHandler
    HomeNewSongsHandler
    ```
  - **Loading fix**: Callback completion thay vì timeout
    ```java
    final int[] count = {0};
    Runnable onComplete = () -> {
        count[0]++;
        if (count[0] >= 4) setLoading(false);
    };
    handler.loadData(onComplete);
    ```

- **LibraryFragment**: Playlists, Liked, History
- **SearchFragment**: Real-time search
- **ProfileFragment**: User info, settings

**4.4.3. PlayerActivity** (1.5 trang)
- **Handler Pattern** (tách logic):
  - **PlayerControlHandler**: Play/pause/next/prev
  - **PlayerSeekBarHandler**: Progress tracking
  - **PlayerLikeHandler**: Toggle favorite
  - **PlayerVolumeHandler**: 
    ```java
    AudioManager audioManager;
    SeekBar volumeSeekBar;
    // Volume up/down buttons
    ```
  - **PlayerImageHandler**: Album art + Palette colors

**Before/After Refactor**:
```
Before: PlayerActivity.java (500+ lines)
After:  PlayerActivity.java (150 lines)
        + 7 handler classes
```

**4.4.4. Adapters** (0.5 trang)
- SongAdapter (grid cards)
- SongListAdapter (horizontal list - artist detail)
- AlbumAdapter, PlaylistAdapter

---

### **CHƯƠNG 5: TỐI ƯU HÓA CODE** (3-4 trang)

#### **5.1. Vấn đề ban đầu** (1 trang)
- **Code lặp lại**:
  - Toast: 57 chỗ
  - Glide: 19 chỗ
  - Validation: scattered
- **Activities quá lớn**: PlayerActivity 500+ lines
- **Hardcoded strings**: "songs", "albums"...
- **Loading spinner stuck**: Fixed timeout không chính xác

#### **5.2. Giải pháp Refactor** (2 trang)

**5.2.1. Tạo Utility Classes** (0.5 trang)
- Centralize duplicate code
- Example: ToastUtils thay thế 57 Toast calls

**5.2.2. Handler Pattern** (1 trang)
- **PlayerActivity**: 1 activity → 1 activity + 7 handlers
- **HomeFragment**: 1 fragment → 1 fragment + 5 handlers
- **Lợi ích**:
  - Mỗi class 1 trách nhiệm (Single Responsibility)
  - Dễ test
  - Dễ maintain

**5.2.3. Fix Loading Issue** (0.5 trang)
**Before**:
```java
// Fixed timeout - sai!
postDelayed(() -> setLoading(false), 800);
```

**After**:
```java
// Callback-based - đúng!
handler1.loadData(onComplete);
handler2.loadData(onComplete);
// setLoading(false) khi TẤT CẢ handlers complete
```

#### **5.3. Kết quả** (0.5 trang)
- Code giảm ~30%
- Dễ đọc, dễ maintain
- Không còn duplicate code
- Loading hoạt động chính xác

---

### **CHƯƠNG 6: TESTING VÀ KẾT QUẢ** (3-4 trang)

#### **6.1. Testing** (1.5 trang)
**Test cases chính**:
- ✅ Đăng ký/Đăng nhập
- ✅ Phát nhạc (play/pause/next/prev)
- ✅ Tạo playlist, thêm/xóa bài
- ✅ Tìm kiếm bài hát
- ✅ Yêu thích bài hát
- ✅ Điều chỉnh âm lượng
- ✅ Xử lý lỗi mạng (bật/tắt WiFi)
- ✅ Loading spinner (không bị stuck)

#### **6.2. Screenshots** (1.5 trang)
Hình ảnh các màn hình chính:
- Login/Register
- Home (slider, albums, songs)
- Library (playlists, liked, history)
- Search
- Full Player
- Artist Detail (horizontal song list)
- Profile

#### **6.3. Đánh giá** (0.5 trang)
- **Performance**: Tải nhanh, mượt mà
- **UX**: Giao diện đẹp, dễ dùng
- **Stability**: Xử lý lỗi tốt

---

### **CHƯƠNG 7: KẾT LUẬN** (2 trang)

#### **7.1. Kết quả đạt được** (1 trang)
- ✅ Hoàn thành 11 tính năng chính
- ✅ Code clean, organized
- ✅ Firebase integration thành công
- ✅ Refactor từ monolithic → modular
- ✅ Network error handling

#### **7.2. Hạn chế** (0.5 trang)
- Chưa có offline playback
- Chưa có recommendation
- Testing chưa đầy đủ

#### **7.3. Hướng phát triển** (0.5 trang)
- **Tính năng**: Offline mode, lyrics, equalizer
- **Kỹ thuật**: ExoPlayer, caching, unit tests
- **Công nghệ**: Migrate Kotlin, Jetpack Compose

---

### **PHỤ LỤC** (2 trang)

#### **Phụ lục A: Project Structure** (0.5 trang)
Chi tiết cấu trúc files

#### **Phụ lục B: Code Examples** (1 trang)
- Repository pattern code
- Handler pattern code
- Callback loading code

#### **Phụ lục C: Hướng dẫn cài đặt** (0.5 trang)
- Clone project
- Firebase setup
- Build & Run

---

### **TÀI LIỆU THAM KHẢO** (1 trang)
1. Android Developer Documentation - https://developer.android.com
2. Firebase Documentation - https://firebase.google.com/docs
3. Material Design Guidelines - https://material.io
4. Glide Documentation - https://bumptech.github.io/glide

---

## **TỔNG KẾT**

| Phần | Số trang |
|------|----------|
| Mở đầu | 3 |
| Chương 1: Tổng quan | 3-4 |
| Chương 2: Cơ sở lý thuyết | 3-4 |
| Chương 3: Thiết kế | 4-5 |
| Chương 4: Triển khai | 8-10 |
| Chương 5: Tối ưu code | 3-4 |
| Chương 6: Testing & Kết quả | 3-4 |
| Chương 7: Kết luận | 2 |
| Phụ lục | 2 |
| Tài liệu tham khảo | 1 |
| **TỔNG** | **25-30** |

---

## **GỢI Ý VIẾT**

### **Điểm nổi bật cần nhấn mạnh**
1. ✨ **Handler Pattern**: Refactor PlayerActivity từ 500 → 150 lines
2. ✨ **Callback Loading**: Fix loading spinner stuck issue
3. ✨ **Network Handling**: Preemptive check thay vì timeout
4. ✨ **Utility Classes**: Giảm duplicate code 30%

### **Diagrams tối thiểu cần có**
- Kiến trúc 3 lớp (UI-Data-Logic)
- Firestore Collections ER Diagram
- Navigation Flow
- Before/After Refactor comparison

### **Code snippets quan trọng**
- Repository với NetworkUtils
- Handler pattern implementation
- Callback-based loading
- MusicPlayer singleton

### **Lời khuyên**
- **Chương 4** quan trọng nhất → viết chi tiết
- **Chương 5** (Refactor) là điểm nhấn → show before/after rõ ràng
- Screenshots thật, đẹp → tăng điểm thuyết trình
- Code mẫu ngắn gọn, highlight điểm chính

---

## **CHECKLIST HOÀN THÀNH BÁO CÁO**

### **Nội dung**
- [ ] Viết xong 7 chương
- [ ] Tạo đầy đủ diagrams (4 diagrams tối thiểu)
- [ ] Chụp screenshots tất cả màn hình
- [ ] Code examples đầy đủ, chạy được
- [ ] Phụ lục hoàn chỉnh

### **Định dạng**
- [ ] Font: Times New Roman, size 13
- [ ] Line spacing: 1.5
- [ ] Margin: 2cm (all sides)
- [ ] Đánh số trang
- [ ] Căn lề đều

### **Trước khi nộp**
- [ ] Kiểm tra chính tả
- [ ] Format code đẹp, có syntax highlighting
- [ ] Hình ảnh rõ nét, có caption
- [ ] Tài liệu tham khảo đầy đủ
- [ ] Export PDF

---

**Báo cáo này vừa đủ chi tiết cho sinh viên đại học, không quá dài dòng nhưng vẫn thể hiện đầy đủ công việc đã làm!** 🎓
