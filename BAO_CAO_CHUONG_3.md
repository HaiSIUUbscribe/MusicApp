# CHƯƠNG 3: THIẾT KẾ HỆ THỐNG

## **3.1. Kiến trúc ứng dụng**

### **3.1.1. Tổng quan kiến trúc**

Ứng dụng Music Player được thiết kế theo mô hình **3-layer architecture** (kiến trúc 3 lớp), đây là một pattern phổ biến trong phát triển ứng dụng Android, giúp tách biệt các concerns và tăng tính maintainability.

**Sơ đồ kiến trúc tổng thể**:

```
┌─────────────────────────────────────────────────────────────┐
│                      UI LAYER                                │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │  Activities  │  │  Fragments   │  │   Adapters   │      │
│  │              │  │              │  │              │      │
│  │ - Login      │  │ - Home       │  │ - Song       │      │
│  │ - Register   │  │ - Library    │  │ - Album      │      │
│  │ - Player     │  │ - Search     │  │ - Playlist   │      │
│  │ - Playlist   │  │ - Profile    │  │ - Artist     │      │
│  │ - Album      │  │ - MiniPlayer │  │ - Slider     │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
│                           ↕                                  │
│                      Handlers                                │
│  ┌──────────────────────────────────────────────────────┐   │
│  │ Home Handlers, Library Handlers, Player Handlers     │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                            ↕
┌─────────────────────────────────────────────────────────────┐
│                   BUSINESS LOGIC LAYER                       │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │ MusicPlayer  │  │ Playlist     │  │   Utilities  │      │
│  │ (Singleton)  │  │ Manager      │  │              │      │
│  │              │  │ (Singleton)  │  │ - ImageLoader│      │
│  │ - play()     │  │              │  │ - Toast      │      │
│  │ - pause()    │  │ - setList()  │  │ - Formatter  │      │
│  │ - next()     │  │ - getNext()  │  │ - Validation │      │
│  │ - seekTo()   │  │ - shuffle()  │  │ - Network    │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└─────────────────────────────────────────────────────────────┘
                            ↕
┌─────────────────────────────────────────────────────────────┐
│                      DATA LAYER                              │
│  ┌────────────────────────────────────────────────────┐     │
│  │              Repositories                          │     │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐          │     │
│  │  │   Song   │ │ Playlist │ │ Favorite │          │     │
│  │  │Repository│ │Repository│ │Repository│          │     │
│  │  └──────────┘ └──────────┘ └──────────┘          │     │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐          │     │
│  │  │  Album   │ │  Artist  │ │ History  │          │     │
│  │  │Repository│ │Repository│ │Repository│          │     │
│  │  └──────────┘ └──────────┘ └──────────┘          │     │
│  │  ┌──────────┐ ┌──────────┐                       │     │
│  │  │   Auth   │ │   User   │                       │     │
│  │  │Repository│ │Repository│                       │     │
│  │  └──────────┘ └──────────┘                       │     │
│  └────────────────────────────────────────────────────┘     │
│                           ↕                                  │
│  ┌────────────────────────────────────────────────────┐     │
│  │              Firebase Services                     │     │
│  │  ┌──────────────┐ ┌──────────────┐ ┌──────────┐  │     │
│  │  │   Firebase   │ │    Cloud     │ │ Firebase │  │     │
│  │  │     Auth     │ │  Firestore   │ │ Storage  │  │     │
│  │  └──────────────┘ └──────────────┘ └──────────┘  │     │
│  └────────────────────────────────────────────────────┘     │
│                                                              │
│  ┌────────────────────────────────────────────────────┐     │
│  │                  Models                            │     │
│  │    Song, Album, Artist, Playlist, User, History   │     │
│  └────────────────────────────────────────────────────┘     │
└─────────────────────────────────────────────────────────────┘
```

### **3.1.2. Chi tiết các lớp**

#### **UI Layer (Presentation Layer)**

**Trách nhiệm**:
- Hiển thị dữ liệu cho người dùng
- Xử lý user interactions (clicks, swipes...)
- Cập nhật UI khi data thay đổi
- Điều hướng giữa các màn hình

**Thành phần**:

1. **Activities**: Màn hình độc lập, chứa logic lifecycle
   - `LoginActivity`: Đăng nhập
   - `RegisterActivity`: Đăng ký
   - `MainActivity`: Container cho fragments với bottom navigation
   - `PlayerActivity`: Màn hình phát nhạc full screen
   - `PlaylistDetailActivity`: Chi tiết playlist
   - `AlbumDetailActivity`: Chi tiết album
   - `ArtistDetailActivity`: Chi tiết nghệ sĩ
   - `UploadSongActivity`: Upload bài hát

2. **Fragments**: Phần của UI có thể tái sử dụng
   - `HomeFragment`: Trang chủ với slider, albums, trending
   - `LibraryFragment`: Thư viện (playlists, liked, history)
   - `SearchFragment`: Tìm kiếm
   - `ProfileFragment`: Thông tin user
   - `MiniPlayerFragment`: Player mini ở bottom

3. **Adapters**: Bind data vào RecyclerView
   - `SongAdapter`: Hiển thị grid cards
   - `SongListAdapter`: Hiển thị horizontal list
   - `AlbumAdapter`: Grid albums
   - `PlaylistAdapter`: Danh sách playlists
   - `ArtistAdapter`: Danh sách nghệ sĩ
   - `SliderAdapter`: Banner slider

4. **Handlers**: Tách logic xử lý UI
   - **Home Handlers**: `HomeAlbumsHandler`, `HomeArtistsHandler`, `HomePopularHandler`, `HomeNewSongsHandler`, `HomeSliderHandler`
   - **Library Handlers**: `LibraryPlaylistHandler`, `LibraryLikedHandler`, `LibraryHistoryHandler`
   - **Player Handlers**: `PlayerControlHandler`, `PlayerSeekBarHandler`, `PlayerLikeHandler`, `PlayerVolumeHandler`, `PlayerImageHandler`
   - **Upload Handlers**: `UploadFilePickerHandler`, `UploadMetadataHandler`, `UploadFirebaseHandler`

**Nguyên tắc**:
- UI không chứa business logic
- Gọi repositories thông qua callbacks
- Handlers giúp tách responsibilities
- ViewBinding cho type safety

#### **Business Logic Layer**

**Trách nhiệm**:
- Xử lý logic nghiệp vụ
- Quản lý trạng thái ứng dụng
- Điều phối giữa UI và Data layer

**Thành phần**:

1. **MusicPlayer (Singleton)**:
   - Quản lý MediaPlayer instance
   - Xử lý playback: play, pause, seek, next, previous
   - Quản lý trạng thái phát nhạc
   - Notify UI khi có thay đổi

2. **PlaylistManager (Singleton)**:
   - Quản lý playlist hiện tại đang phát
   - Shuffle và repeat modes
   - Track current position, next/previous song
   - Không lưu vào database (chỉ runtime state)

3. **Utility Classes**:
   - `ImageLoader`: Wrapper cho Glide, centralize image loading
   - `ToastUtils`: Hiển thị thông báo nhất quán
   - `TimeFormatter`: Format duration, play count, time ago
   - `ValidationUtils`: Validate email, password, input
   - `NetworkUtils`: Kiểm tra kết nối mạng
   - `PermissionUtils`: Xử lý permissions
   - `Logger`: Logging wrapper

**Đặc điểm**:
- MusicPlayer và PlaylistManager là Singleton
- Utilities là static methods (stateless)
- Không phụ thuộc vào Android framework (dễ test)

#### **Data Layer**

**Trách nhiệm**:
- Truy cập và quản lý dữ liệu
- Tương tác với Firebase (Firestore, Storage, Auth)
- Cache data khi cần
- Cung cấp interface đơn giản cho upper layers

**Thành phần**:

1. **Repositories**: Abstract data sources
   - `SongRepository`: CRUD bài hát, queries (trending, new, search)
   - `AlbumRepository`: Quản lý albums
   - `ArtistRepository`: Quản lý nghệ sĩ
   - `PlaylistRepository`: CRUD playlists
   - `FavoriteRepository`: Like/unlike songs
   - `HistoryRepository`: Lưu và đọc lịch sử
   - `AuthRepository`: Đăng ký, đăng nhập
   - `UserRepository`: Quản lý thông tin user
   - `SongUploadRepository`: Upload bài hát mới

2. **Models**: Data classes
   - `Song`: id, title, artist, album, duration, audioUrl, imageUrl, playCount...
   - `Album`: id, name, artist, imageUrl, songs[]
   - `Artist`: id, name, imageUrl, followers, songs[]
   - `Playlist`: id, name, userId, description, songs[], createdAt
   - `User`: id, email, displayName, photoUrl, createdAt
   - `History`: userId, songId, playedAt

3. **Firebase Services**:
   - `FirebaseAuth`: Authentication service
   - `FirebaseFirestore`: Database service
   - `FirebaseStorage`: File storage service

**Nguyên tắc**:
- Repository Pattern: Hide implementation details
- Callback-based async operations
- Network check before Firestore queries
- Consistent error handling

### **3.1.3. Data Flow**

**Luồng dữ liệu khi load Trending Songs**:

```
1. User mở HomeFragment
   ↓
2. HomeFragment gọi HomePopularHandler.loadData()
   ↓
3. HomePopularHandler gọi SongRepository.getTrendingSongs()
   ↓
4. SongRepository kiểm tra NetworkUtils.isNetworkAvailable()
   ↓
5. Nếu có mạng → Query Firestore
   ↓
6. Firestore trả về QuerySnapshot
   ↓
7. SongRepository convert DocumentSnapshot → List<Song>
   ↓
8. Callback onSuccess(songs)
   ↓
9. HomePopularHandler nhận songs
   ↓
10. Update SongAdapter.setSongs(songs)
    ↓
11. RecyclerView hiển thị songs
```

**Luồng dữ liệu khi phát nhạc**:

```
1. User click vào Song trong RecyclerView
   ↓
2. Adapter gọi OnSongClickListener
   ↓
3. Fragment/Activity nhận callback
   ↓
4. Tạo Intent với songId
   ↓
5. Mở PlayerActivity
   ↓
6. PlayerActivity.onCreate()
   ↓
7. Load song info từ Intent hoặc SongRepository
   ↓
8. PlaylistManager.setPlaylist(songs)
   ↓
9. MusicPlayer.play(song.audioUrl)
   ↓
10. MediaPlayer stream audio từ Firebase Storage
    ↓
11. PlayerSeekBarHandler bắt đầu track progress
    ↓
12. UI update: album art, title, artist, controls
    ↓
13. User tương tác: pause, next, seek...
    ↓
14. PlayerControlHandler xử lý → gọi MusicPlayer methods
```

---

## **3.2. Cấu trúc thư mục**

### **3.2.1. Package Structure**

```
com.example.musicapplication/
│
├── 📁 constants/
│   ├── AppConstants.java          # Limits, timeouts, formats
│   ├── FirebaseConstants.java     # Collection/field names
│   └── IntentKeys.java             # Intent extra keys
│
├── 📁 data/
│   ├── 📁 repository/
│   │   ├── AlbumRepository.java
│   │   ├── ArtistRepository.java
│   │   ├── AuthRepository.java
│   │   ├── FavoriteRepository.java
│   │   ├── HistoryRepository.java
│   │   ├── PlaylistRepository.java
│   │   ├── ProfileRepository.java
│   │   ├── SearchRepository.java
│   │   ├── SongRepository.java
│   │   ├── SongUploadRepository.java
│   │   └── UserRepository.java
│   │
│   └── 📁 services/
│       ├── FirebaseStorageService.java
│       └── StorageService.java    # Interface
│
├── 📁 model/
│   ├── Album.java
│   ├── Artist.java
│   ├── Genre.java
│   ├── History.java
│   ├── Playlist.java
│   ├── SliderItem.java
│   ├── Song.java
│   └── User.java
│
├── 📁 player/
│   ├── MusicPlayer.java           # Singleton MediaPlayer wrapper
│   └── PlaylistManager.java       # Singleton playlist state
│
├── 📁 ui/
│   ├── 📁 activity/
│   │   ├── 📁 album/
│   │   │   ├── AlbumDetailActivity.java
│   │   │   └── AllAlbumsActivity.java
│   │   ├── 📁 auth/
│   │   │   ├── LoginActivity.java
│   │   │   └── RegisterActivity.java
│   │   ├── 📁 genre/
│   │   │   └── GenreDetailActivity.java
│   │   ├── 📁 main/
│   │   │   └── MainActivity.java
│   │   ├── 📁 player/
│   │   │   ├── PlayerActivity.java
│   │   │   └── 📁 handlers/
│   │   │       ├── PlayerControlHandler.java
│   │   │       ├── PlayerSeekBarHandler.java
│   │   │       ├── PlayerLikeHandler.java
│   │   │       ├── PlayerVolumeHandler.java
│   │   │       ├── PlayerImageHandler.java
│   │   │       ├── PlayerPlaylistHandler.java
│   │   │       ├── PlayerShareHandler.java
│   │   │       └── PlayerDownloadHandler.java
│   │   ├── 📁 playlist/
│   │   │   ├── PlaylistDetailActivity.java
│   │   │   └── AddSongPlaylistActivity.java
│   │   ├── 📁 upload/
│   │   │   ├── UploadSongActivity.java
│   │   │   └── 📁 handlers/
│   │   │       ├── UploadFilePickerHandler.java
│   │   │       ├── UploadMetadataHandler.java
│   │   │       ├── UploadFirebaseHandler.java
│   │   │       └── UploadValidationHandler.java
│   │   ├── 📁 other/
│   │   │   ├── AboutActivity.java
│   │   │   └── PrivacyActivity.java
│   │   └── ArtistDetailActivity.java
│   │
│   ├── 📁 adapter/
│   │   ├── AlbumAdapter.java
│   │   ├── ArtistAdapter.java
│   │   ├── GenreAdapter.java
│   │   ├── PlaylistAdapter.java
│   │   ├── SliderAdapter.java
│   │   ├── SongAdapter.java
│   │   ├── SongListAdapter.java
│   │   └── ViewPagerAdapter.java
│   │
│   └── 📁 fragments/
│       ├── 📁 home/
│       │   ├── HomeFragment.java
│       │   └── 📁 handlers/
│       │       ├── HomeAlbumsHandler.java
│       │       ├── HomeArtistsHandler.java
│       │       ├── HomeNewSongsHandler.java
│       │       ├── HomePopularHandler.java
│       │       ├── HomeProfileHandler.java
│       │       └── HomeSliderHandler.java
│       ├── 📁 library/
│       │   ├── LibraryFragment.java
│       │   └── 📁 handlers/
│       │       ├── LibraryHistoryHandler.java
│       │       ├── LibraryLikedHandler.java
│       │       ├── LibraryPlaylistHandler.java
│       │       ├── LibraryProfileHandler.java
│       │       └── LibrarySearchHandler.java
│       ├── 📁 profile/
│       │   ├── ProfileFragment.java
│       │   └── 📁 handlers/
│       │       ├── ProfileInfoHandler.java
│       │       ├── ProfileSettingsHandler.java
│       │       └── ProfileStatsHandler.java
│       ├── MiniPlayerFragment.java
│       └── SearchFragment.java
│
├── 📁 utils/
│   ├── ImageLoader.java          # Glide wrapper
│   ├── Logger.java                # Logging utility
│   ├── NetworkUtils.java          # Network checking
│   ├── PermissionUtils.java       # Permission handling
│   ├── TimeFormatter.java         # Time/number formatting
│   ├── ToastUtils.java            # Toast messages
│   └── ValidationUtils.java       # Input validation
│
└── MusicApplication.java          # Application class
```

### **3.2.2. Giải thích cấu trúc**

**constants/**:
- Centralize magic numbers, strings
- Dễ dàng thay đổi giá trị cấu hình
- Tránh hardcode trong code

**data/repository/**:
- Mỗi repository quản lý một loại dữ liệu
- Đóng gói Firestore queries
- Cung cấp interface clean cho UI layer

**model/**:
- POJOs (Plain Old Java Objects)
- Serializable để truyền qua Intent
- Firestore mapping với annotations

**player/**:
- Business logic của music playback
- Singleton instances
- Không phụ thuộc Android framework

**ui/activity/**:
- Tổ chức theo tính năng (auth, player, upload...)
- Mỗi package có handlers riêng
- Tách biệt concerns

**ui/fragments/**:
- Mỗi fragment có package riêng
- Handlers trong sub-package
- Dễ maintain và scale

**utils/**:
- Helper classes tái sử dụng
- Static methods (stateless)
- Independent, không phụ thuộc lẫn nhau

---

## **3.3. Cơ sở dữ liệu Firestore**

### **3.3.1. Collections và Schema**

**1. Collection: `users`**

Lưu trữ thông tin người dùng.

```javascript
users/{userId}
{
  "userId": "auto-generated-id",
  "email": "user@example.com",
  "displayName": "John Doe",
  "photoUrl": "https://...",
  "createdAt": Timestamp,
  "updatedAt": Timestamp,
  "bio": "Music lover",
  "likedSongs": ["songId1", "songId2", "songId3"],  // Array of song IDs
  "playlists": ["playlistId1", "playlistId2"]       // Array of playlist IDs
}
```

**Fields**:
- `userId` (String): ID duy nhất, match với Firebase Auth UID
- `email` (String): Email đăng ký
- `displayName` (String): Tên hiển thị
- `photoUrl` (String): URL ảnh đại diện
- `createdAt` (Timestamp): Ngày tạo tài khoản
- `likedSongs` (Array): Danh sách bài hát yêu thích
- `playlists` (Array): Danh sách playlist đã tạo

**2. Collection: `songs`**

Lưu trữ thông tin bài hát.

```javascript
songs/{songId}
{
  "songId": "auto-generated-id",
  "title": "Song Title",
  "artist": "Artist Name",
  "album": "Album Name",
  "genre": "Pop",
  "duration": 180000,                    // milliseconds
  "audioUrl": "https://storage...",
  "imageUrl": "https://storage...",
  "playCount": "1500",                   // String type (Firestore limitation)
  "uploadDate": Timestamp,
  "uploadedBy": "userId",
  "lyrics": "Song lyrics...",            // Optional
  "tags": ["love", "sad", "ballad"]     // Array for searching
}
```

**Fields**:
- `title` (String): Tên bài hát
- `artist` (String): Tên nghệ sĩ
- `album` (String): Tên album
- `duration` (Number): Độ dài (milliseconds)
- `audioUrl` (String): URL file audio trên Firebase Storage
- `imageUrl` (String): URL ảnh album art
- `playCount` (String): Số lượt nghe (String do crash khi dùng Number)
- `uploadDate` (Timestamp): Ngày upload
- `tags` (Array): Tags để tìm kiếm

**3. Collection: `albums`**

Lưu trữ thông tin album.

```javascript
albums/{albumId}
{
  "albumId": "auto-generated-id",
  "name": "Album Name",
  "artist": "Artist Name",
  "imageUrl": "https://...",
  "releaseYear": "2024",
  "genre": "Pop",
  "songs": ["songId1", "songId2", "songId3"],  // Array of song IDs
  "songCount": "10",
  "createdAt": Timestamp
}
```

**4. Collection: `artists`**

Lưu trữ thông tin nghệ sĩ.

```javascript
artists/{artistId}
{
  "artistId": "auto-generated-id",
  "name": "Artist Name",
  "imageUrl": "https://...",
  "bio": "Artist biography...",
  "followers": "50000",                  // String type
  "genres": ["Pop", "R&B"],
  "songs": ["songId1", "songId2"],
  "albums": ["albumId1", "albumId2"],
  "createdAt": Timestamp
}
```

**5. Collection: `playlists`**

Lưu trữ playlists của người dùng.

```javascript
playlists/{playlistId}
{
  "playlistId": "auto-generated-id",
  "userId": "userId",                    // Owner
  "name": "My Favorite Songs",
  "description": "Songs I love",
  "imageUrl": "https://...",             // Từ bài đầu tiên hoặc custom
  "songs": ["songId1", "songId2"],       // Ordered array
  "songCount": "25",
  "isPublic": false,
  "createdAt": Timestamp,
  "updatedAt": Timestamp
}
```

**6. Collection: `history`**

Lưu trữ lịch sử nghe nhạc của người dùng.

```javascript
history/{historyId}
{
  "historyId": "auto-generated-id",
  "userId": "userId",
  "songId": "songId",
  "playedAt": Timestamp,
  "duration": 180000,                    // Song duration snapshot
  "completionPercentage": 75             // % of song listened
}
```

**Query pattern**:
```java
// Lấy 50 bài gần nhất của user
db.collection("history")
  .whereEqualTo("userId", currentUserId)
  .orderBy("playedAt", Query.Direction.DESCENDING)
  .limit(50)
  .get();
```

### **3.3.2. Indexes (Chỉ mục)**

Firestore yêu cầu **composite indexes** cho queries có nhiều điều kiện hoặc sắp xếp.

**Index 1: Songs by Artist and PlayCount**
```
Collection: songs
Fields:
- artist (Ascending)
- playCount (Descending)
```

**Sử dụng cho query**:
```java
db.collection("songs")
  .whereEqualTo("artist", "Taylor Swift")
  .orderBy("playCount", Query.Direction.DESCENDING)
  .limit(10);
```

**Index 2: History by User and Time**
```
Collection: history
Fields:
- userId (Ascending)
- playedAt (Descending)
```

**Index 3: Playlists by User and Update Time**
```
Collection: playlists
Fields:
- userId (Ascending)
- updatedAt (Descending)
```

**Cách tạo Index**:
1. Chạy query trong app
2. Firestore báo lỗi và cung cấp link
3. Click link → Firebase Console tự động mở
4. Confirm tạo index
5. Đợi 2-5 phút để index được build
6. Query hoạt động bình thường

### **3.3.3. Security Rules**

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    
    // Users
    match /users/{userId} {
      allow read: if request.auth != null;
      allow write: if request.auth.uid == userId;
    }
    
    // Songs - public read, authenticated write
    match /songs/{songId} {
      allow read: if true;  // Public
      allow create: if request.auth != null;
      allow update, delete: if request.auth.uid == resource.data.uploadedBy;
    }
    
    // Albums - public read
    match /albums/{albumId} {
      allow read: if true;
      allow write: if request.auth != null;
    }
    
    // Playlists - owner only
    match /playlists/{playlistId} {
      allow read: if request.auth != null;
      allow write: if request.auth.uid == resource.data.userId;
    }
    
    // History - owner only
    match /history/{historyId} {
      allow read, write: if request.auth.uid == resource.data.userId;
    }
  }
}
```

---

## **3.4. Thiết kế giao diện**

### **3.4.1. Luồng điều hướng**

**Navigation Flow Diagram**:

```
                    App Launch
                        ↓
                  [Splash Screen]
                        ↓
              Check Authentication
                    ↙       ↘
            Not Logged       Logged In
                ↓                ↓
         [LoginActivity]   [MainActivity]
                ↓                ↓
         [RegisterActivity]      ↓
                ↓           ┌────┴────┐
                └──────────→│Bottom Nav│
                            └────┬────┘
                    ┌────────────┼────────────┐
                    ↓            ↓            ↓
              [HomeFragment] [LibraryFragment] [SearchFragment] [ProfileFragment]
                    │            │            │            │
                    ├→ Albums    ├→ Playlists ├→ Results  ├→ Settings
                    ├→ Artists   ├→ Liked     │           └→ Logout
                    └→ Trending  └→ History   │
                         ↓            ↓        ↓
                    [AlbumDetail][PlaylistDetail][SongList]
                         ↓            ↓        ↓
                      Click Song on List
                         ↓
                    [PlayerActivity] ←─── [MiniPlayerFragment]
                         │                      ↑
                         ├→ Like               │
                         ├→ Add to Playlist    │
                         ├→ Share              │
                         └→ Download (future)  │
                                               │
                    [UploadSongActivity] ──────┘
```

**Chi tiết luồng chính**:

**1. Authentication Flow**:
```
Splash → Check Auth
  ├→ Not logged → LoginActivity
  │                    ↓
  │              [Register] button → RegisterActivity
  │                    ↓
  │              Create account
  │                    ↓
  └→ Logged in → MainActivity
```

**2. Main Navigation (Bottom Navigation)**:
```
MainActivity
├── Home Tab → HomeFragment
│   ├── Slider (auto-scroll banners)
│   ├── Popular Albums → AlbumDetailActivity
│   ├── Trending Artists → ArtistDetailActivity
│   ├── New Songs → Click → PlayerActivity
│   └── Top Songs → Click → PlayerActivity
│
├── Library Tab → LibraryFragment
│   ├── My Playlists → PlaylistDetailActivity
│   ├── Liked Songs → Song List → PlayerActivity
│   └── History → Song List → PlayerActivity
│
├── Search Tab → SearchFragment
│   ├── Search input (real-time)
│   └── Results → Click → PlayerActivity
│
└── Profile Tab → ProfileFragment
    ├── User info
    ├── Stats (songs played, playlists created)
    ├── Settings → AboutActivity, PrivacyActivity
    └── Logout → LoginActivity
```

**3. Player Flow**:
```
Any Song Click
    ↓
Open PlayerActivity
    ├── Load song data
    ├── Set playlist context
    ├── Start playback
    ↓
User interactions:
    ├── Play/Pause
    ├── Next/Previous
    ├── Seek
    ├── Volume control
    ├── Like → Update Firestore
    ├── Add to Playlist → AddSongPlaylistActivity
    └── Share → Android Share Sheet
```

**4. Upload Flow**:
```
HomeFragment → FAB (Upload button)
    ↓
UploadSongActivity
    ├── Pick audio file (from storage)
    ├── Pick image (from gallery/camera)
    ├── Fill metadata (title, artist, album, genre)
    ├── Validate inputs
    ├── Upload to Firebase Storage (with progress)
    ├── Save metadata to Firestore
    └── Success → Return to Home
```

### **3.4.2. Wireframes màn hình chính**

**1. Login Screen**:
```
┌─────────────────────────────────┐
│                                 │
│         [App Logo/Icon]         │
│                                 │
│      Music Player App           │
│                                 │
│   ┌─────────────────────────┐   │
│   │  Email                  │   │
│   └─────────────────────────┘   │
│                                 │
│   ┌─────────────────────────┐   │
│   │  Password               │   │
│   └─────────────────────────┘   │
│                                 │
│   ┌─────────────────────────┐   │
│   │    LOGIN BUTTON         │   │
│   └─────────────────────────┘   │
│                                 │
│   Don't have account? Register  │
│                                 │
└─────────────────────────────────┘
```

**2. Home Screen**:
```
┌─────────────────────────────────┐
│ [Profile]    Home    [Search]   │ ← Toolbar
├─────────────────────────────────┤
│ ┌─────────────────────────────┐ │
│ │   [Banner Slider]           │ │ ← Auto-scroll
│ └─────────────────────────────┘ │
│                                 │
│ Popular Albums          [More]  │
│ ┌───┐ ┌───┐ ┌───┐ ┌───┐       │
│ │ 🎵│ │ 🎵│ │ 🎵│ │ 🎵│ →     │ ← Horizontal scroll
│ └───┘ └───┘ └───┘ └───┘       │
│                                 │
│ Trending Artists        [More]  │
│ ○ ○ ○ ○ →                      │ ← Circular avatars
│                                 │
│ New Songs                       │
│ ┌───────────────────────────┐   │
│ │ 🎵 Song 1  Artist  3:45   │   │
│ ├───────────────────────────┤   │
│ │ 🎵 Song 2  Artist  4:20   │   │
│ └───────────────────────────┘   │
├─────────────────────────────────┤
│ [▶] Now Playing: Song Title     │ ← Mini Player
├─────────────────────────────────┤
│ [🏠] [📚] [🔍] [👤]             │ ← Bottom Nav
└─────────────────────────────────┘
```

**3. Player Screen (Full)**:
```
┌─────────────────────────────────┐
│ [← Back]              [⋮ More]  │
├─────────────────────────────────┤
│                                 │
│                                 │
│       ┌───────────────┐         │
│       │               │         │
│       │  Album Art    │         │ ← Large image
│       │               │         │
│       └───────────────┘         │
│                                 │
│        Song Title               │
│        Artist Name              │
│                                 │
│   ──────●─────────────          │ ← SeekBar
│   1:30           3:45           │
│                                 │
│     [🔀] [⏮] [⏯] [⏭] [🔁]      │ ← Controls
│                                 │
│   ──────●─────────────  🔊      │ ← Volume
│                                 │
│     [♥]  [+]  [↗]  [⬇]          │ ← Actions
│                                 │
└─────────────────────────────────┘
```

**4. Library Screen**:
```
┌─────────────────────────────────┐
│          Library                │
│  [Profile] [Search]             │
├─────────────────────────────────┤
│                                 │
│ My Playlists            [+ New] │
│ ┌─────────────────────────────┐ │
│ │ 📁 Favorites      25 songs  │ │
│ ├─────────────────────────────┤ │
│ │ 📁 Workout Mix    12 songs  │ │
│ ├─────────────────────────────┤ │
│ │ 📁 Chill Vibes    30 songs  │ │
│ └─────────────────────────────┘ │
│                                 │
│ Liked Songs                     │
│ ┌─────────────────────────────┐ │
│ │ ♥ 45 songs                  │ │
│ └─────────────────────────────┘ │
│                                 │
│ Recently Played                 │
│ ┌─────────────────────────────┐ │
│ │ 🎵 Song  Artist  2 hrs ago  │ │
│ ├─────────────────────────────┤ │
│ │ 🎵 Song  Artist  5 hrs ago  │ │
│ └─────────────────────────────┘ │
├─────────────────────────────────┤
│ [▶] Now Playing: Song Title     │
├─────────────────────────────────┤
│ [🏠] [📚] [🔍] [👤]             │
└─────────────────────────────────┘
```

**5. Search Screen**:
```
┌─────────────────────────────────┐
│ [← ]  Search songs, artists...  │ ← Search input
├─────────────────────────────────┤
│                                 │
│ Results (23)                    │
│                                 │
│ ┌─────────────────────────────┐ │
│ │ 🎵  Song Title               │ │
│ │     Artist • Album  3:45    │ │
│ ├─────────────────────────────┤ │
│ │ 🎵  Another Song             │ │
│ │     Artist • Album  4:20    │ │
│ ├─────────────────────────────┤ │
│ │ 🎵  Third Song               │ │
│ │     Artist • Album  2:30    │ │
│ └─────────────────────────────┘ │
│                                 │
│ (Empty state if no results)     │
│   🔍 No songs found             │
│   Try different keywords        │
│                                 │
├─────────────────────────────────┤
│ [🏠] [📚] [🔍] [👤]             │
└─────────────────────────────────┘
```

### **3.4.3. Design Guidelines**

**Color Palette**:
- **Primary**: `#6200EE` (Purple) - Nút chính, highlights
- **Primary Variant**: `#3700B3` (Dark Purple) - App bar
- **Secondary**: `#03DAC6` (Teal) - FAB, accents
- **Background**: `#FFFFFF` (White) - Màu nền chính
- **Surface**: `#F5F5F5` (Light Gray) - Cards
- **Error**: `#B00020` (Red) - Error messages

**Typography**:
- **Titles**: Roboto Bold, 24sp
- **Body**: Roboto Regular, 16sp
- **Captions**: Roboto Light, 12sp

**Spacing**:
- **Small**: 8dp
- **Medium**: 16dp
- **Large**: 24dp

**Components**:
- **Cards**: 8dp corner radius, 4dp elevation
- **Buttons**: 4dp corner radius
- **Images**: Circle (avatars), Rounded 8dp (album arts)

---

## **Tóm tắt Chương 3**

Chương 3 đã trình bày chi tiết thiết kế hệ thống:

**Kiến trúc 3 lớp**:
- **UI Layer**: Activities, Fragments, Adapters, Handlers
- **Business Logic Layer**: MusicPlayer, PlaylistManager, Utilities
- **Data Layer**: Repositories, Models, Firebase Services

**Cấu trúc thư mục**:
- Tổ chức theo tính năng (feature-based)
- Handlers tách riêng trong sub-packages
- Clear separation of concerns

**Firestore Database**:
- 6 collections: users, songs, albums, artists, playlists, history
- Composite indexes cho queries phức tạp
- Security Rules bảo vệ dữ liệu

**Giao diện**:
- Navigation flow rõ ràng
- Bottom Navigation với 4 tabs
- Wireframes cho các màn hình chính
- Material Design guidelines

Thiết kế này đảm bảo ứng dụng có cấu trúc rõ ràng, dễ mở rộng và bảo trì. Chương tiếp theo sẽ trình bày quá trình triển khai chi tiết từng thành phần.

---

**[Next: Chương 4 - Triển khai hệ thống]**
