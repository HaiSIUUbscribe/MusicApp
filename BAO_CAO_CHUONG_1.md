# CHƯƠNG 1: TỔNG QUAN ĐỀ TÀI

## **1.1. Giới thiệu**

### **1.1.1. Bối cảnh**

Trong những năm gần đây, ngành công nghiệp âm nhạc số đã có những bước phát triển vượt bậc. Theo thống kê của IFPI (International Federation of the Phonographic Industry), doanh thu từ âm nhạc trực tuyến chiếm hơn 67% tổng doanh thu toàn cầu vào năm 2024, với mức tăng trưởng đều đặn qua các năm. Xu hướng nghe nhạc đã chuyển dịch mạnh mẽ từ các phương tiện truyền thống như CD, radio sang các nền tảng streaming trực tuyến.

Người dùng ngày nay mong muốn có thể truy cập vào hàng triệu bài hát mọi lúc, mọi nơi thông qua thiết bị di động. Các ứng dụng nghe nhạc như Spotify, Apple Music, YouTube Music đã trở thành một phần không thể thiếu trong đời sống hàng ngày của hàng tỷ người dùng trên toàn thế giới. Sự tiện lợi, tính cá nhân hóa và khả năng chia sẻ âm nhạc là những yếu tố then chốt giúp các ứng dụng này thành công.

### **1.1.2. Động lực thực hiện**

Việc xây dựng một ứng dụng nghe nhạc trên nền tảng Android không chỉ mang lại giá trị thực tiễn mà còn là cơ hội tuyệt vời để:

- **Học hỏi và áp dụng kiến thức**: Ứng dụng âm nhạc là một dự án toàn diện, đòi hỏi sử dụng nhiều thành phần của Android SDK như Activity, Fragment, RecyclerView, MediaPlayer, cùng với các kỹ thuật xử lý bất đồng bộ, quản lý vòng đời, và thiết kế giao diện.

- **Thực hành kiến trúc phần mềm**: Dự án cung cấp môi trường lý tưởng để áp dụng các design patterns như Repository Pattern, Singleton Pattern, Handler Pattern, giúp tổ chức code một cách khoa học và dễ bảo trì.

- **Tích hợp công nghệ Cloud**: Firebase là một BaaS (Backend-as-a-Service) mạnh mẽ, cho phép xây dựng ứng dụng có backend đầy đủ tính năng mà không cần viết server code, rất phù hợp cho các dự án học tập và startup.

- **Nâng cao kỹ năng thực tế**: Quá trình phát triển từ thiết kế, coding, refactor đến testing giúp rèn luyện tư duy giải quyết vấn đề và kỹ năng làm việc với một dự án có quy mô thực tế.

### **1.1.3. Vấn đề cần giải quyết**

Mặc dù có nhiều ứng dụng nghe nhạc sẵn có, việc xây dựng một ứng dụng riêng giúp:

1. **Kiểm soát hoàn toàn**: Tùy chỉnh tính năng theo nhu cầu cụ thể
2. **Học tập chuyên sâu**: Hiểu rõ cách thức hoạt động của một music player từ cơ bản đến nâng cao
3. **Tối ưu hóa code**: Áp dụng best practices trong software engineering
4. **Chuẩn bị cho công việc**: Làm quen với quy trình phát triển ứng dụng thực tế

---

## **1.2. Mục tiêu đề tài**

### **1.2.1. Mục tiêu tổng quát**

Xây dựng một ứng dụng nghe nhạc hoàn chỉnh trên nền tảng Android, tích hợp Firebase Backend, có giao diện thân thiện và code được tổ chức theo kiến trúc sạch (Clean Architecture), dễ bảo trì và mở rộng.

### **1.2.2. Mục tiêu cụ thể**

1. **Về tính năng**:
   - Xây dựng đầy đủ các chức năng cơ bản của một music player: phát nhạc, tạm dừng, chuyển bài, shuffle, repeat
   - Triển khai hệ thống quản lý người dùng với đăng ký, đăng nhập
   - Phát triển tính năng quản lý playlist, yêu thích bài hát, lưu lịch sử nghe nhạc
   - Tích hợp tìm kiếm bài hát theo nhiều tiêu chí
   - Cho phép người dùng upload bài hát lên hệ thống

2. **Về công nghệ**:
   - Sử dụng Firebase Authentication để quản lý người dùng
   - Áp dụng Cloud Firestore làm cơ sở dữ liệu NoSQL
   - Tích hợp Firebase Storage để lưu trữ file âm thanh và hình ảnh
   - Sử dụng thư viện Glide để tối ưu việc load và cache ảnh

3. **Về kiến trúc code**:
   - Áp dụng Repository Pattern để tách biệt data layer
   - Sử dụng Handler Pattern để tách logic xử lý UI
   - Tạo các Utility classes để tái sử dụng code
   - Refactor code từ monolithic sang modular architecture

4. **Về chất lượng**:
   - Code sạch, dễ đọc, tuân thủ Java naming conventions
   - Xử lý lỗi đầy đủ, đặc biệt là lỗi network
   - Tối ưu performance và user experience
   - Testing các tính năng chính

---

## **1.3. Tính năng chính của ứng dụng**

### **1.3.1. Quản lý người dùng**

**Đăng ký tài khoản**:
- Người dùng có thể tạo tài khoản mới với email và mật khẩu
- Validation đầu vào: kiểm tra format email, độ dài mật khẩu (tối thiểu 6 ký tự)
- Xử lý các trường hợp lỗi: email đã tồn tại, mật khẩu yếu, lỗi kết nối

**Đăng nhập**:
- Xác thực người dùng qua Firebase Authentication
- Lưu trạng thái đăng nhập (session management)
- Tự động chuyển đến màn hình chính nếu đã đăng nhập

**Quản lý profile**:
- Hiển thị thông tin cá nhân: tên, email, ảnh đại diện
- Thống kê: số bài hát đã nghe, số playlist đã tạo
- Cài đặt: đăng xuất, chính sách bảo mật

### **1.3.2. Phát nhạc**

**Playback controls**:
- **Play/Pause**: Phát và tạm dừng bài hát
- **Next/Previous**: Chuyển sang bài tiếp theo hoặc quay lại bài trước
- **Seek**: Tua đến vị trí bất kỳ trong bài hát thông qua SeekBar
- **Shuffle**: Phát ngẫu nhiên các bài trong playlist
- **Repeat**: Lặp lại bài hiện tại hoặc toàn bộ playlist
- **Volume Control**: Điều chỉnh âm lượng với SeekBar và nút tăng/giảm

**Mini Player**:
- Hiển thị ở bottom của màn hình, luôn có thể truy cập
- Thông tin cơ bản: tên bài, nghệ sĩ, album art
- Nút play/pause, next
- Click vào mở Full Player

**Full Player**:
- Hiển thị đầy đủ thông tin bài hát
- Album art lớn với hiệu ứng Palette (màu nền động)
- SeekBar với thời gian hiện tại / tổng thời gian
- Toàn bộ controls (play, pause, next, prev, shuffle, repeat)
- Volume control
- Nút yêu thích, chia sẻ, thêm vào playlist

### **1.3.3. Quản lý Playlist**

**Tạo playlist**:
- Người dùng tạo playlist mới với tên và mô tả
- Validation: tên không được để trống, tối đa 50 ký tự

**Thêm/Xóa bài hát**:
- Thêm bài hát vào playlist từ danh sách tất cả bài hát
- Xóa bài hát khỏi playlist
- Sắp xếp thứ tự bài hát (kéo thả - optional)

**Xóa playlist**:
- Xóa playlist với xác nhận
- Không xóa được các playlist mặc định (Liked Songs)

**Hiển thị**:
- Danh sách tất cả playlist của user
- Số lượng bài hát trong mỗi playlist
- Ảnh đại diện (lấy từ album art của bài đầu tiên)

### **1.3.4. Yêu thích bài hát**

- Toggle like/unlike bằng nút trái tim
- Bài hát yêu thích được lưu vào Firestore
- Hiển thị danh sách "Liked Songs" trong Library
- Sync real-time: like trên một màn hình, cập nhật ngay ở màn hình khác

### **1.3.5. Lịch sử nghe nhạc**

- Tự động lưu lịch sử khi phát bài hát
- Hiển thị danh sách bài đã nghe gần đây
- Sắp xếp theo thời gian (mới nhất trước)
- Giới hạn 50 bài gần nhất để tối ưu performance

### **1.3.6. Tìm kiếm**

**Real-time search**:
- Tìm kiếm bài hát theo tên, nghệ sĩ, album
- Hiển thị kết quả ngay khi gõ (debounce 300ms)
- Highlight từ khóa tìm kiếm trong kết quả

**Filters**:
- Lọc theo loại: bài hát, album, nghệ sĩ (optional)
- Sắp xếp kết quả theo độ phổ biến, tên A-Z

**Empty state**:
- Hiển thị thông báo khi không có kết quả
- Gợi ý kiểm tra chính tả hoặc thử từ khóa khác

### **1.3.7. Upload bài hát**

**Chọn file**:
- Chọn file audio từ storage (MP3, M4A, WAV...)
- Chọn ảnh album từ thư viện hoặc camera
- Validation: kiểm tra định dạng file, kích thước (max 50MB)

**Metadata**:
- Nhập thông tin: tên bài hát, nghệ sĩ, album
- Chọn thể loại (genre)
- Validation: các trường bắt buộc không được để trống

**Upload process**:
- Upload file lên Firebase Storage
- Progress bar hiển thị tiến độ
- Lưu metadata vào Firestore sau khi upload thành công
- Xử lý lỗi: lỗi mạng, hết dung lượng, timeout

### **1.3.8. Duyệt nội dung**

**Home screen**:
- **Slider**: Banner quảng cáo hoặc bài hát nổi bật (auto-scroll 3s)
- **Popular Albums**: Danh sách album phổ biến (RecyclerView horizontal)
- **Trending Artists**: Nghệ sĩ đang hot
- **New Songs**: Bài hát mới upload gần đây
- **Top Songs**: Bài hát có lượt nghe cao nhất

**Library screen**:
- **My Playlists**: Danh sách playlist đã tạo
- **Liked Songs**: Bài hát yêu thích
- **History**: Lịch sử nghe nhạc

**Artist Detail**:
- Thông tin nghệ sĩ
- Danh sách bài hát của nghệ sĩ (horizontal list)
- Số lượng followers (optional)

**Album Detail**:
- Ảnh bìa album
- Thông tin: tên album, nghệ sĩ, năm phát hành
- Danh sách bài hát trong album

---

## **1.4. Công nghệ sử dụng**

### **1.4.1. Nền tảng và Ngôn ngữ**

**Android SDK**:
- **Version**: compileSdk 36, minSdk 27, targetSdk 36
- **Lý do chọn**: 
  - API 27+ chiếm >90% thiết bị Android hiện nay
  - Hỗ trợ đầy đủ Material Design components
  - Tương thích với các thư viện hiện đại

**Java**:
- **Version**: Java 11
- **Lý do chọn**:
  - Ngôn ngữ chính thức của Android (trước Kotlin)
  - Tài liệu phong phú, cộng đồng lớn
  - Phù hợp cho sinh viên đã học Java cơ bản

### **1.4.2. Backend - Firebase Platform**

**Firebase Authentication**:
- Quản lý người dùng với email/password
- Xử lý đăng ký, đăng nhập, đăng xuất
- Session management tự động

**Cloud Firestore**:
- NoSQL database, lưu trữ dạng document
- Real-time synchronization
- Offline support (caching)
- Scalable, không cần setup server

**Firebase Storage**:
- Lưu trữ file âm thanh (MP3, M4A...)
- Lưu trữ hình ảnh (album art, avatar)
- CDN tích hợp sẵn, tốc độ download nhanh

**Lý do chọn Firebase**:
- Miễn phí với quota hợp lý cho học tập
- Setup đơn giản, không cần backend code
- Tích hợp tốt với Android
- Bảo mật tốt với Security Rules

### **1.4.3. Thư viện bên thứ ba**

**Glide**:
- **Version**: 4.16.0
- **Chức năng**: Load và cache hình ảnh
- **Ưu điểm**: 
  - Performance cao, tự động cache
  - Hỗ trợ placeholder, error image
  - Transform (crop, rounded corners...)

**Material Design Components**:
- **Version**: 1.12.0
- **Chức năng**: UI components chuẩn Material Design
- **Bao gồm**: 
  - BottomNavigationView
  - CollapsingToolbarLayout
  - CardView
  - FloatingActionButton

**AndroidX Libraries**:
- AppCompat: Tương thích ngược
- ConstraintLayout: Responsive layout
- RecyclerView: Danh sách hiệu năng cao
- ViewPager2: Swipe giữa các fragments

**Palette API**:
- Extract màu từ ảnh
- Tạo background động cho Player screen

### **1.4.4. Công cụ phát triển**

**Android Studio**:
- IDE chính thức cho Android development
- Version: Latest stable (Hedgehog | 2023.1.1+)
- Tích hợp Gradle build system

**Gradle**:
- Build automation tool
- Quản lý dependencies
- Build variants (debug/release)

**Git & GitHub**:
- Version control system
- Lưu trữ source code
- Quản lý phiên bản, branches

**Firebase Console**:
- Quản lý Firestore database
- Upload test data
- Monitor usage, analytics

---

## **1.5. Phạm vi dự án**

### **1.5.1. Phạm vi về tính năng**

**Các tính năng được triển khai**:
- ✅ Quản lý người dùng (đăng ký, đăng nhập, profile)
- ✅ Phát nhạc cơ bản (play, pause, next, previous, seek)
- ✅ Phát nhạc nâng cao (shuffle, repeat, volume control)
- ✅ Quản lý playlist (CRUD operations)
- ✅ Yêu thích bài hát
- ✅ Lịch sử nghe nhạc
- ✅ Tìm kiếm real-time
- ✅ Upload bài hát
- ✅ Duyệt nội dung (albums, artists, trending)
- ✅ Network error handling
- ✅ Loading states

**Các tính năng KHÔNG triển khai** (ngoài phạm vi):
- ❌ Offline playback (cache audio files)
- ❌ Background playback service (phát nhạc khi tắt màn hình)
- ❌ Recommendation algorithm
- ❌ Social features (follow, share, comments)
- ❌ Lyrics display
- ❌ Equalizer
- ❌ Sleep timer
- ❌ Download songs
- ❌ Multiple language support

### **1.5.2. Phạm vi về nền tảng**

**Hệ điều hành**:
- Android 8.0 (Oreo, API 27) trở lên
- Tương thích với Android 14 (API 34+)

**Thiết bị**:
- Smartphone (phone layout)
- Không hỗ trợ tablet layout (sẽ scale lên)
- Orientation: Portrait mode (không hỗ trợ landscape tối ưu)

**Kết nối**:
- **Bắt buộc** kết nối internet để:
  - Đăng nhập/đăng ký
  - Load danh sách bài hát
  - Stream audio
  - Upload bài hát
- Có xử lý offline: hiển thị lỗi "Không có kết nối mạng"

### **1.5.3. Phạm vi về người dùng**

**Người dùng mục tiêu**:
- Sinh viên, người trẻ yêu thích âm nhạc
- Độ tuổi: 18-30
- Quen thuộc với smartphone

**Giới hạn**:
- Single user session (không đồng bộ nhiều thiết bị)
- Không phân quyền (admin/user)
- Không giới hạn số lượng user đăng ký

### **1.5.4. Phạm vi về thời gian**

**Thời gian thực hiện**: 8-10 tuần

**Phân bổ**:
- Tuần 1-2: Nghiên cứu, thiết kế, setup project
- Tuần 3-5: Phát triển core features (auth, player, playlist)
- Tuần 6-7: Phát triển features phụ (search, upload, profile)
- Tuần 8: Refactor code, tối ưu hóa
- Tuần 9: Testing, fix bugs
- Tuần 10: Hoàn thiện báo cáo, chuẩn bị demo

### **1.5.5. Phạm vi về dữ liệu**

**Firestore Collections**:
- `users`: Thông tin người dùng
- `songs`: Khoảng 50-100 bài hát mẫu
- `albums`: 10-20 albums
- `artists`: 15-25 nghệ sĩ
- `playlists`: User-generated
- `history`: Lưu trữ 50 bài gần nhất/user

**Storage**:
- Test data: ~500MB audio + images
- User uploads: Giới hạn 50MB/file

**Performance targets**:
- App startup: < 2 giây
- Load danh sách: < 1 giây
- Start playback: < 3 giây
- Search response: < 500ms

---

## **Tóm tắt Chương 1**

Chương 1 đã trình bày tổng quan về đề tài xây dựng ứng dụng nghe nhạc trên Android. Dự án hướng đến việc phát triển một music player đầy đủ tính năng với backend Firebase, áp dụng kiến trúc code sạch và best practices. Các tính năng chính bao gồm quản lý người dùng, phát nhạc với đầy đủ controls, quản lý playlist, yêu thích, lịch sử, tìm kiếm và upload bài hát. 

Công nghệ sử dụng bao gồm Android SDK (API 27+), Java, Firebase (Authentication, Firestore, Storage), và các thư viện như Glide, Material Design. Phạm vi dự án được giới hạn rõ ràng về tính năng, nền tảng, và thời gian thực hiện để đảm bảo tính khả thi.

Chương tiếp theo sẽ trình bày cơ sở lý thuyết về Android, Firebase và các design patterns được sử dụng trong dự án.

---

**[Next: Chương 2 - Cơ sở lý thuyết]**
