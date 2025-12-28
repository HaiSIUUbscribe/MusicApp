# CHƯƠNG 2: CƠ SỞ LÝ THUYẾT

## **2.1. Nền tảng Android**

### **2.1.1. Activity và Fragment**

**Activity** là một trong những thành phần cơ bản nhất của Android, đại diện cho một màn hình trong ứng dụng. Mỗi Activity có vòng đời (lifecycle) riêng được quản lý bởi hệ thống Android.

**Vòng đời Activity (Activity Lifecycle)**:

Activity trải qua nhiều trạng thái trong quá trình tồn tại:

1. **onCreate()**: Được gọi khi Activity được tạo lần đầu tiên
   - Khởi tạo UI, bind views với ViewBinding
   - Setup data, khởi tạo repositories
   - Chỉ được gọi một lần trong vòng đời

2. **onStart()**: Activity sắp hiển thị lên màn hình
   - Bắt đầu animations
   - Register broadcast receivers

3. **onResume()**: Activity đang tương tác với người dùng
   - Bắt đầu cập nhật UI
   - Resume playback (nếu có)
   - Trạng thái "foreground", hoạt động tích cực

4. **onPause()**: Activity mất focus
   - Pause playback
   - Lưu draft data
   - Dừng animations nặng

5. **onStop()**: Activity không còn hiển thị
   - Giải phóng tài nguyên nặng
   - Unregister listeners

6. **onDestroy()**: Activity bị hủy
   - Cleanup cuối cùng
   - Giải phóng memory

**Ví dụ trong dự án - PlayerActivity**:
```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivityPlayerBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    
    initHandlers(); // Khởi tạo các handlers
    loadSongData(); // Load thông tin bài hát
}

@Override
protected void onPause() {
    super.onPause();
    // Không pause nhạc vì user vẫn muốn nghe khi minimize
}

@Override
protected void onDestroy() {
    super.onDestroy();
    // Cleanup handlers, remove callbacks
    if (seekBarHandler != null) {
        seekBarHandler.cleanup();
    }
}
```

**Fragment** là một phần của UI có thể tái sử dụng trong Activity. Một Activity có thể chứa nhiều Fragments.

**Ưu điểm của Fragment**:
- **Modular**: Tách UI thành các phần nhỏ, độc lập
- **Reusable**: Sử dụng lại trong nhiều Activities
- **Adaptive**: Dễ dàng tạo UI responsive cho tablet/phone

**Vòng đời Fragment**:
Tương tự Activity nhưng phức tạp hơn vì phụ thuộc vào Activity container:
- onAttach() → onCreate() → onCreateView() → onViewCreated() → onStart() → onResume()
- onPause() → onStop() → onDestroyView() → onDestroy() → onDetach()

**Ví dụ trong dự án - HomeFragment**:
```java
@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container,
                         Bundle savedInstanceState) {
    binding = FragmentHomeBinding.inflate(inflater, container, false);
    return binding.getRoot();
}

@Override
public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    initHandlers();    // Khởi tạo handlers
    loadHomeData();    // Load data
}

@Override
public void onDestroyView() {
    super.onDestroyView();
    binding = null;    // Tránh memory leak
}
```

**Navigation giữa Activities**:
```java
// Chuyển từ HomeFragment sang PlayerActivity
Intent intent = new Intent(requireActivity(), PlayerActivity.class);
intent.putExtra("songId", song.getId());
startActivity(intent);
```

### **2.1.2. RecyclerView**

**RecyclerView** là component hiển thị danh sách dữ liệu lớn một cách hiệu quả bằng cách tái sử dụng (recycle) các views.

**Ưu điểm so với ListView**:
- **Performance cao**: Chỉ tạo views cho items hiển thị trên màn hình
- **ViewHolder pattern**: Bắt buộc, giảm findViewById() calls
- **Flexible**: Hỗ trợ nhiều loại layout (Linear, Grid, Staggered)
- **Animations**: Built-in animations cho add/remove items

**Các thành phần chính**:

1. **RecyclerView**: Container view
2. **Adapter**: Bind data vào views
3. **ViewHolder**: Cache view references
4. **LayoutManager**: Quản lý cách sắp xếp items

**Ví dụ - SongAdapter**:
```java
public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {
    private List<Song> songs;
    private OnSongClickListener listener;
    
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Tạo ViewHolder - chỉ được gọi khi cần view mới
        ItemSongBinding binding = ItemSongBinding.inflate(
            LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }
    
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Bind data vào view đã tồn tại - gọi nhiều lần khi scroll
        Song song = songs.get(position);
        holder.bind(song);
    }
    
    @Override
    public int getItemCount() {
        return songs != null ? songs.size() : 0;
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        private ItemSongBinding binding;
        
        ViewHolder(ItemSongBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        
        void bind(Song song) {
            binding.tvSongTitle.setText(song.getTitle());
            binding.tvArtist.setText(song.getArtist());
            ImageLoader.load(binding.getRoot().getContext(), 
                           song.getImageUrl(), 
                           binding.imgSong);
        }
    }
}
```

**LayoutManager types**:
- **LinearLayoutManager**: Danh sách dọc/ngang (HomeFragment - popular songs)
- **GridLayoutManager**: Lưới (SearchFragment - grid of songs)
- **StaggeredGridLayoutManager**: Lưới không đều (Pinterest-style)

**Trong dự án**:
```java
// HomeFragment - Horizontal list
recyclerViewAlbums.setLayoutManager(
    new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

// SearchFragment - Vertical list
recyclerViewResults.setLayoutManager(new LinearLayoutManager(context));
```

### **2.1.3. ViewBinding**

**ViewBinding** là tính năng cho phép viết code tương tác với views dễ dàng và an toàn hơn so với findViewById().

**Vấn đề với findViewById()**:
```java
// Cách cũ - dễ lỗi
TextView tvTitle = findViewById(R.id.tvTitle);
// Lỗi nếu id sai
// Lỗi nếu cast sai type
// Null pointer nếu view không tồn tại
```

**Giải pháp với ViewBinding**:
```java
// Cách mới - type-safe
ActivityPlayerBinding binding = ActivityPlayerBinding.inflate(getLayoutInflater());
setContentView(binding.getRoot());

// Truy cập views
binding.tvTitle.setText("Song Title");  // Compile-time safety
binding.btnPlay.setOnClickListener(v -> play());
```

**Ưu điểm**:
- **Null safety**: Chỉ tạo binding cho views thực sự tồn tại
- **Type safety**: Compiler kiểm tra type tự động
- **Performance**: Không cần findViewById() - nhanh hơn
- **Code gọn**: Ít boilerplate code

**Setup trong build.gradle**:
```gradle
android {
    buildFeatures {
        viewBinding true
    }
}
```

**Sử dụng trong Activity**:
```java
public class PlayerActivity extends AppCompatActivity {
    private ActivityPlayerBinding binding;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Truy cập views
        binding.btnPlay.setOnClickListener(this::onPlayClick);
        binding.seekBar.setOnSeekBarChangeListener(seekBarListener);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null; // Cleanup
    }
}
```

**Sử dụng trong Fragment**:
```java
public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Quan trọng! Tránh memory leak
    }
}
```

### **2.1.4. Material Design**

**Material Design** là hệ thống thiết kế do Google phát triển, cung cấp guidelines và components để xây dựng UI đẹp, nhất quán trên Android.

**Nguyên tắc cơ bản**:
1. **Material is the metaphor**: UI giống vật liệu thực (giấy, thẻ)
2. **Bold, graphic, intentional**: Typography rõ ràng, màu sắc đậm
3. **Motion provides meaning**: Animations phản ánh hành vi thực tế

**Components sử dụng trong dự án**:

**1. BottomNavigationView**:
```xml
<com.google.android.material.bottomnavigation.BottomNavigationView
    android:id="@+id/bottomNavigation"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:menu="@menu/bottom_navigation" />
```

**2. CardView**:
```xml
<androidx.cardview.widget.CardView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:cardCornerRadius="8dp"
    app:cardElevation="4dp">
    <!-- Nội dung card -->
</androidx.cardview.widget.CardView>
```

**3. FloatingActionButton**:
```xml
<com.google.android.material.floatingactionbutton.FloatingActionButton
    android:id="@+id/fabPlay"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:src="@drawable/ic_play" />
```

**4. CollapsingToolbarLayout**:
```xml
<com.google.android.material.appbar.CollapsingToolbarLayout
    android:layout_width="match_parent"
    android:layout_height="300dp"
    app:layout_scrollFlags="scroll|exitUntilCollapsed">
    
    <ImageView
        android:id="@+id/imgAlbumArt"
        app:layout_collapseMode="parallax" />
        
    <androidx.appcompat.widget.Toolbar
        app:layout_collapseMode="pin" />
</com.google.android.material.appbar.CollapsingToolbarLayout>
```

**Material Colors**:
```xml
<!-- colors.xml -->
<color name="colorPrimary">#6200EE</color>
<color name="colorPrimaryVariant">#3700B3</color>
<color name="colorOnPrimary">#FFFFFF</color>
<color name="colorSecondary">#03DAC6</color>
```

---

## **2.2. Firebase Platform**

Firebase là Backend-as-a-Service (BaaS) của Google, cung cấp các dịch vụ backend sẵn có mà không cần viết server code.

### **2.2.1. Firebase Authentication**

**Firebase Authentication** cung cấp hệ thống xác thực người dùng với nhiều phương thức: email/password, Google, Facebook, phone...

**Ưu điểm**:
- **Dễ setup**: Chỉ cần vài dòng code
- **Bảo mật**: Google quản lý encryption, token
- **Session management**: Tự động lưu trạng thái đăng nhập
- **Multi-platform**: Sync giữa Android, iOS, Web

**Email/Password Authentication**:

**Đăng ký**:
```java
FirebaseAuth auth = FirebaseAuth.getInstance();

auth.createUserWithEmailAndPassword(email, password)
    .addOnCompleteListener(task -> {
        if (task.isSuccessful()) {
            // Đăng ký thành công
            FirebaseUser user = auth.getCurrentUser();
            String userId = user.getUid();
            // Lưu thông tin user vào Firestore
        } else {
            // Xử lý lỗi
            String error = task.getException().getMessage();
            // "The email address is already in use"
            // "The password is too weak"
        }
    });
```

**Đăng nhập**:
```java
auth.signInWithEmailAndPassword(email, password)
    .addOnCompleteListener(task -> {
        if (task.isSuccessful()) {
            // Đăng nhập thành công
            FirebaseUser user = auth.getCurrentUser();
            // Chuyển đến MainActivity
        } else {
            // Sai email hoặc password
            ToastUtils.showError(this, "Đăng nhập thất bại");
        }
    });
```

**Kiểm tra trạng thái đăng nhập**:
```java
FirebaseUser currentUser = auth.getCurrentUser();
if (currentUser != null) {
    // Đã đăng nhập
    String email = currentUser.getEmail();
    String userId = currentUser.getUid();
} else {
    // Chưa đăng nhập
    startActivity(new Intent(this, LoginActivity.class));
}
```

**Đăng xuất**:
```java
FirebaseAuth.getInstance().signOut();
```

**AuthRepository trong dự án**:
```java
public class AuthRepository {
    private FirebaseAuth auth;
    
    public void register(String email, String password, 
                        AuthCallback callback) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener(result -> {
                callback.onSuccess(result.getUser());
            })
            .addOnFailureListener(e -> {
                callback.onError(e.getMessage());
            });
    }
    
    public interface AuthCallback {
        void onSuccess(FirebaseUser user);
        void onError(String error);
    }
}
```

### **2.2.2. Cloud Firestore**

**Cloud Firestore** là NoSQL document database, lưu trữ dữ liệu dạng JSON-like documents trong collections.

**Đặc điểm**:
- **NoSQL**: Không có schema cố định
- **Real-time**: Tự động sync khi data thay đổi
- **Offline support**: Cache data locally
- **Scalable**: Tự động scale theo traffic
- **Query mạnh mẽ**: Filter, sort, pagination

**Cấu trúc dữ liệu**:
```
Firestore
├── collections
│   ├── users (collection)
│   │   ├── userId1 (document)
│   │   │   ├── email: "user@example.com"
│   │   │   ├── displayName: "John Doe"
│   │   │   └── createdAt: Timestamp
│   │   └── userId2 (document)
│   │
│   ├── songs (collection)
│   │   ├── songId1 (document)
│   │   │   ├── title: "Song Name"
│   │   │   ├── artist: "Artist Name"
│   │   │   ├── duration: 180000
│   │   │   ├── audioUrl: "https://..."
│   │   │   ├── imageUrl: "https://..."
│   │   │   └── playCount: 1500
│   │   └── songId2 (document)
```

**CRUD Operations**:

**1. Create (Thêm document)**:
```java
FirebaseFirestore db = FirebaseFirestore.getInstance();

Map<String, Object> song = new HashMap<>();
song.put("title", "Song Title");
song.put("artist", "Artist Name");
song.put("duration", 180000);
song.put("playCount", 0);
song.put("uploadDate", FieldValue.serverTimestamp());

db.collection("songs")
    .add(song)  // Auto-generate ID
    .addOnSuccessListener(docRef -> {
        String songId = docRef.getId();
    })
    .addOnFailureListener(e -> {
        // Xử lý lỗi
    });
```

**2. Read (Đọc documents)**:

Đọc một document:
```java
db.collection("songs")
    .document(songId)
    .get()
    .addOnSuccessListener(document -> {
        if (document.exists()) {
            String title = document.getString("title");
            String artist = document.getString("artist");
            Long duration = document.getLong("duration");
        }
    });
```

Đọc nhiều documents với query:
```java
db.collection("songs")
    .whereEqualTo("artist", "Taylor Swift")
    .orderBy("playCount", Query.Direction.DESCENDING)
    .limit(10)
    .get()
    .addOnSuccessListener(querySnapshot -> {
        List<Song> songs = new ArrayList<>();
        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
            Song song = doc.toObject(Song.class);
            songs.add(song);
        }
    });
```

**3. Update**:
```java
db.collection("songs")
    .document(songId)
    .update("playCount", FieldValue.increment(1))  // Tăng 1
    .addOnSuccessListener(aVoid -> {
        // Update thành công
    });
```

**4. Delete**:
```java
db.collection("playlists")
    .document(playlistId)
    .delete()
    .addOnSuccessListener(aVoid -> {
        ToastUtils.showSuccess(context, "Đã xóa playlist");
    });
```

**Real-time Listeners**:
```java
// Lắng nghe thay đổi real-time
ListenerRegistration listener = db.collection("songs")
    .document(songId)
    .addSnapshotListener((snapshot, error) -> {
        if (snapshot != null && snapshot.exists()) {
            // Data đã thay đổi
            Song song = snapshot.toObject(Song.class);
            updateUI(song);
        }
    });

// Cleanup khi không cần
@Override
protected void onDestroy() {
    super.onDestroy();
    if (listener != null) {
        listener.remove();
    }
}
```

**Composite Index**:
Khi query có nhiều điều kiện hoặc sắp xếp, cần tạo composite index:
```java
// Query này cần composite index
db.collection("songs")
    .whereEqualTo("artist", "Artist Name")
    .orderBy("playCount", Query.Direction.DESCENDING);

// Firebase sẽ báo lỗi và cung cấp link tạo index
```

### **2.2.3. Firebase Storage**

**Firebase Storage** lưu trữ và phục vụ file (images, audio, video) với CDN tích hợp.

**Ưu điểm**:
- **Scalable**: Lưu trữ không giới hạn
- **CDN**: Download nhanh từ nhiều location
- **Resumable uploads**: Tiếp tục upload nếu bị ngắt
- **Security Rules**: Kiểm soát truy cập

**Cấu trúc Storage**:
```
gs://your-app.appspot.com/
├── audio/
│   ├── song1.mp3
│   ├── song2.mp3
├── images/
│   ├── albums/
│   │   ├── album1.jpg
│   └── avatars/
│       ├── user1.jpg
```

**Upload file**:
```java
FirebaseStorage storage = FirebaseStorage.getInstance();
StorageReference storageRef = storage.getReference();

// Tạo reference đến file
StorageReference audioRef = storageRef.child("audio/" + fileName);

// Upload file
Uri audioUri = Uri.fromFile(new File(audioPath));

UploadTask uploadTask = audioRef.putFile(audioUri);

// Lắng nghe tiến độ
uploadTask.addOnProgressListener(snapshot -> {
    double progress = (100.0 * snapshot.getBytesTransferred()) 
                      / snapshot.getTotalByteCount();
    progressBar.setProgress((int) progress);
})
.addOnSuccessListener(taskSnapshot -> {
    // Upload thành công, lấy download URL
    audioRef.getDownloadUrl()
        .addOnSuccessListener(uri -> {
            String downloadUrl = uri.toString();
            // Lưu URL vào Firestore
        });
})
.addOnFailureListener(e -> {
    ToastUtils.showError(context, "Upload thất bại: " + e.getMessage());
});
```

**Download URL**:
```java
StorageReference imageRef = storageRef.child("images/albums/album1.jpg");

imageRef.getDownloadUrl()
    .addOnSuccessListener(uri -> {
        String url = uri.toString();
        // Load ảnh với Glide
        ImageLoader.load(context, url, imageView);
    });
```

**Delete file**:
```java
StorageReference fileRef = storageRef.child("audio/song.mp3");

fileRef.delete()
    .addOnSuccessListener(aVoid -> {
        // Xóa thành công
    })
    .addOnFailureListener(e -> {
        // Xóa thất bại
    });
```

---

## **2.3. Design Patterns**

### **2.3.1. Repository Pattern**

**Repository Pattern** là pattern tách biệt logic truy cập dữ liệu (data access logic) khỏi business logic, cung cấp interface đơn giản để làm việc với data.

**Vấn đề**:
- Activities/Fragments trực tiếp gọi Firestore
- Code lặp lại nhiều
- Khó test
- Khó thay đổi data source (Firebase → SQLite)

**Giải pháp với Repository**:
```
UI Layer (Activity/Fragment)
    ↓
Repository (Interface trừu tượng)
    ↓
Data Source (Firestore, SQLite, API...)
```

**Ưu điểm**:
- **Separation of concerns**: UI không biết data từ đâu
- **Testable**: Mock repository dễ dàng
- **Maintainable**: Thay đổi data source không ảnh hưởng UI
- **Reusable**: Dùng repository ở nhiều nơi

**Ví dụ - SongRepository**:
```java
public class SongRepository {
    private FirebaseFirestore firestore;
    
    public SongRepository() {
        firestore = FirebaseFirestore.getInstance();
    }
    
    // Interface callback
    public interface SongsCallback {
        void onSuccess(List<Song> songs);
        void onError(String error);
    }
    
    // Method trừu tượng hóa Firestore query
    public void getTrendingSongs(int limit, SongsCallback callback) {
        // Kiểm tra network trước
        if (!NetworkUtils.isNetworkAvailable(context)) {
            callback.onError("Không có kết nối mạng");
            return;
        }
        
        firestore.collection("songs")
            .orderBy("playCount", Query.Direction.DESCENDING)
            .limit(limit)
            .get()
            .addOnSuccessListener(querySnapshot -> {
                List<Song> songs = new ArrayList<>();
                for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                    Song song = doc.toObject(Song.class);
                    song.setId(doc.getId());
                    songs.add(song);
                }
                callback.onSuccess(songs);
            })
            .addOnFailureListener(e -> {
                callback.onError(e.getMessage());
            });
    }
    
    public void searchSongs(String query, SongsCallback callback) {
        // Implementation
    }
    
    public void getSongsByArtist(String artist, SongsCallback callback) {
        // Implementation
    }
}
```

**Sử dụng trong Activity**:
```java
public class HomeFragment extends Fragment {
    private SongRepository songRepository;
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        songRepository = new SongRepository();
        loadTrendingSongs();
    }
    
    private void loadTrendingSongs() {
        setLoading(true);
        
        songRepository.getTrendingSongs(10, new SongsCallback() {
            @Override
            public void onSuccess(List<Song> songs) {
                setLoading(false);
                adapter.setSongs(songs);
            }
            
            @Override
            public void onError(String error) {
                setLoading(false);
                ToastUtils.showError(requireContext(), error);
            }
        });
    }
}
```

### **2.3.2. Singleton Pattern**

**Singleton Pattern** đảm bảo một class chỉ có duy nhất một instance trong toàn bộ ứng dụng và cung cấp global access point.

**Khi nào dùng**:
- Cần share state giữa nhiều components
- Quản lý resource chung (Database, MusicPlayer)
- Tránh tạo nhiều instances không cần thiết

**Ví dụ - MusicPlayer Singleton**:
```java
public class MusicPlayer {
    private static MusicPlayer instance;
    private MediaPlayer mediaPlayer;
    private Song currentSong;
    private boolean isPlaying = false;
    
    // Private constructor - ngăn tạo instance từ bên ngoài
    private MusicPlayer() {
        mediaPlayer = new MediaPlayer();
    }
    
    // Public method để lấy instance duy nhất
    public static synchronized MusicPlayer getInstance() {
        if (instance == null) {
            instance = new MusicPlayer();
        }
        return instance;
    }
    
    public void play(String audioUrl) {
        try {
            if (isPlaying) {
                mediaPlayer.stop();
                mediaPlayer.reset();
            }
            
            mediaPlayer.setDataSource(audioUrl);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                isPlaying = true;
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void pause() {
        if (isPlaying) {
            mediaPlayer.pause();
            isPlaying = false;
        }
    }
    
    public void resume() {
        if (!isPlaying) {
            mediaPlayer.start();
            isPlaying = true;
        }
    }
    
    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }
    
    public int getDuration() {
        return mediaPlayer.getDuration();
    }
    
    public boolean isPlaying() {
        return isPlaying;
    }
}
```

**Sử dụng**:
```java
// Bất kỳ đâu trong app
MusicPlayer player = MusicPlayer.getInstance();
player.play(song.getAudioUrl());

// Tất cả đều trỏ đến cùng một instance
MusicPlayer player1 = MusicPlayer.getInstance();
MusicPlayer player2 = MusicPlayer.getInstance();
// player1 == player2 → true
```

**Lưu ý**:
- Thread-safe với `synchronized`
- Cleanup khi app bị destroy
- Cẩn thận với memory leaks (lưu context)

### **2.3.3. Handler Pattern**

**Handler Pattern** (hoặc Delegate Pattern) tách logic xử lý thành các class riêng biệt, mỗi class chịu trách nhiệm một chức năng cụ thể.

**Vấn đề**:
```java
// PlayerActivity.java - 500+ lines
public class PlayerActivity extends AppCompatActivity {
    // Quá nhiều trách nhiệm:
    // - Xử lý play/pause/next/prev
    // - Cập nhật SeekBar
    // - Xử lý like/unlike
    // - Load ảnh và extract màu
    // - Điều chỉnh volume
    // - Share song
    // - Add to playlist
    // → Khó đọc, khó maintain
}
```

**Giải pháp - Tách thành Handlers**:
```
PlayerActivity (150 lines - coordinator)
├── PlayerControlHandler (play/pause/next/prev)
├── PlayerSeekBarHandler (progress tracking)
├── PlayerLikeHandler (favorite toggle)
├── PlayerVolumeHandler (volume control)
├── PlayerImageHandler (album art + palette)
├── PlayerPlaylistHandler (add to playlist)
└── PlayerShareHandler (share song)
```

**Ví dụ - PlayerControlHandler**:
```java
public class PlayerControlHandler {
    private ImageView btnPlay;
    private ImageView btnNext;
    private ImageView btnPrevious;
    private MusicPlayer player;
    private PlaylistManager playlistManager;
    
    public PlayerControlHandler(View view) {
        btnPlay = view.findViewById(R.id.btnPlay);
        btnNext = view.findViewById(R.id.btnNext);
        btnPrevious = view.findViewById(R.id.btnPrevious);
        
        player = MusicPlayer.getInstance();
        playlistManager = PlaylistManager.getInstance();
        
        setupListeners();
    }
    
    private void setupListeners() {
        btnPlay.setOnClickListener(v -> togglePlayPause());
        btnNext.setOnClickListener(v -> playNext());
        btnPrevious.setOnClickListener(v -> playPrevious());
    }
    
    private void togglePlayPause() {
        if (player.isPlaying()) {
            player.pause();
            btnPlay.setImageResource(R.drawable.ic_play);
        } else {
            player.resume();
            btnPlay.setImageResource(R.drawable.ic_pause);
        }
    }
    
    private void playNext() {
        Song nextSong = playlistManager.getNextSong();
        if (nextSong != null) {
            player.play(nextSong.getAudioUrl());
            updateUI(nextSong);
        }
    }
    
    private void playPrevious() {
        Song prevSong = playlistManager.getPreviousSong();
        if (prevSong != null) {
            player.play(prevSong.getAudioUrl());
            updateUI(prevSong);
        }
    }
    
    public void updatePlayPauseButton() {
        if (player.isPlaying()) {
            btnPlay.setImageResource(R.drawable.ic_pause);
        } else {
            btnPlay.setImageResource(R.drawable.ic_play);
        }
    }
}
```

**PlayerActivity sau refactor**:
```java
public class PlayerActivity extends AppCompatActivity {
    private ActivityPlayerBinding binding;
    
    // Handlers
    private PlayerControlHandler controlHandler;
    private PlayerSeekBarHandler seekBarHandler;
    private PlayerLikeHandler likeHandler;
    private PlayerVolumeHandler volumeHandler;
    private PlayerImageHandler imageHandler;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        initHandlers();
        loadSongData();
    }
    
    private void initHandlers() {
        controlHandler = new PlayerControlHandler(binding.getRoot());
        seekBarHandler = new PlayerSeekBarHandler(binding.seekBar, binding.tvCurrentTime, binding.tvDuration);
        likeHandler = new PlayerLikeHandler(binding.btnLike, songId);
        volumeHandler = new PlayerVolumeHandler(this, binding.seekBarVolume, binding.btnVolumeUp, binding.btnVolumeDown);
        imageHandler = new PlayerImageHandler(binding.imgAlbumArt, binding.layoutPlayer);
    }
    
    private void loadSongData() {
        // Load song info
        imageHandler.loadImage(song.getImageUrl());
        seekBarHandler.updateDuration(song.getDuration());
    }
    
    // Activity chỉ còn ~150 lines!
}
```

**Lợi ích**:
- **Single Responsibility**: Mỗi handler một nhiệm vụ
- **Dễ đọc**: Code ngắn gọn, rõ ràng
- **Dễ test**: Test từng handler độc lập
- **Dễ maintain**: Sửa một tính năng không ảnh hưởng phần khác
- **Reusable**: Dùng lại handler ở activities khác

---

## **Tóm tắt Chương 2**

Chương 2 đã trình bày các cơ sở lý thuyết cần thiết cho dự án:

**Android Components**:
- **Activity/Fragment**: Lifecycle, navigation, quản lý UI
- **RecyclerView**: Hiển thị danh sách hiệu quả với Adapter và ViewHolder
- **ViewBinding**: Truy cập views an toàn, type-safe
- **Material Design**: UI components đẹp, nhất quán

**Firebase Platform**:
- **Authentication**: Quản lý user với email/password
- **Firestore**: NoSQL database với real-time sync
- **Storage**: Lưu trữ files với CDN

**Design Patterns**:
- **Repository Pattern**: Tách biệt data access logic
- **Singleton Pattern**: Quản lý MusicPlayer instance duy nhất
- **Handler Pattern**: Tách UI logic thành modules nhỏ

Các kiến thức này là nền tảng để hiểu thiết kế và triển khai ứng dụng trong các chương tiếp theo.

---

**[Next: Chương 3 - Thiết kế hệ thống]**
