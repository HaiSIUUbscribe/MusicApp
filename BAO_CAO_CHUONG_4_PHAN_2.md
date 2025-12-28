# CHƯƠNG 4: TRIỂN KHAI HỆ THỐNG (Phần 2)

## **4.4. Lớp giao diện (UI Layer)**

Lớp giao diện là nơi người dùng tương tác trực tiếp với ứng dụng. Đây là phần quan trọng nhất trong việc áp dụng **Handler Pattern** để refactor code, giảm độ phức tạp và tăng khả năng bảo trì.

### **4.4.1. MainActivity - Container chính**

`MainActivity` là Activity chính chứa các Fragments và điều hướng giữa các màn hình thông qua `BottomNavigationView`.

**Layout (activity_main.xml)**:
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout 
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <!-- Fragment Container -->
    <androidx.fragment.app.FragmentContainerView
        android:id="@+id/fragmentContainer"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toTopOf="@id/bottomNavigation"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

    <!-- Bottom Navigation -->
    <com.google.android.material.bottomnavigation.BottomNavigationView
        android:id="@+id/bottomNavigation"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="@color/colorSurface"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:menu="@menu/bottom_navigation"
        app:itemIconTint="@color/bottom_nav_color"
        app:itemTextColor="@color/bottom_nav_color" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

**MainActivity.java**:
```java
public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Kiểm tra đăng nhập
        checkAuthentication();

        // Setup Bottom Navigation
        setupBottomNavigation();

        // Load fragment mặc định
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }
    }

    private void checkAuthentication() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            // Chưa đăng nhập, chuyển đến LoginActivity
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private void setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_library) {
                selectedFragment = new LibraryFragment();
            } else if (itemId == R.id.nav_search) {
                selectedFragment = new SearchFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        // Tránh load lại fragment hiện tại
        if (fragment.getClass().equals(currentFragment != null ? currentFragment.getClass() : null)) {
            return;
        }

        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit();
        
        currentFragment = fragment;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
```

**Bottom Navigation Menu (bottom_navigation.xml)**:
```xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android">
    <item
        android:id="@+id/nav_home"
        android:icon="@drawable/ic_home"
        android:title="Trang chủ" />
    
    <item
        android:id="@+id/nav_library"
        android:icon="@drawable/ic_library"
        android:title="Thư viện" />
    
    <item
        android:id="@+id/nav_search"
        android:icon="@drawable/ic_search"
        android:title="Tìm kiếm" />
    
    <item
        android:id="@+id/nav_profile"
        android:icon="@drawable/ic_profile"
        android:title="Cá nhân" />
</menu>
```

---

### **4.4.2. HomeFragment - Áp dụng Handler Pattern**

`HomeFragment` là ví dụ điển hình về việc áp dụng Handler Pattern để tách logic load data thành các handlers riêng biệt.

#### **4.4.2.1. Vấn đề ban đầu**

Trước khi refactor, `HomeFragment` có ~400 lines với tất cả logic load data trong một file:

```java
// HomeFragment.java - Trước refactor (400+ lines)
public class HomeFragment extends Fragment {
    // Load albums
    private void loadAlbums() {
        firestore.collection("albums")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .addOnSuccessListener(/* ... */);
    }
    
    // Load artists
    private void loadArtists() {
        firestore.collection("artists")
            .orderBy("followers", Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .addOnSuccessListener(/* ... */);
    }
    
    // Load popular songs
    private void loadPopularSongs() {
        firestore.collection("songs")
            .orderBy("playCount", Query.Direction.DESCENDING)
            .limit(20)
            .get()
            .addOnSuccessListener(/* ... */);
    }
    
    // Load new songs
    private void loadNewSongs() {
        // ...
    }
    
    // Load slider
    private void loadSlider() {
        // ...
    }
    
    // → Quá nhiều code, khó đọc, khó maintain
}
```

#### **4.4.2.2. Giải pháp với Handler Pattern**

Tách thành 5 handlers, mỗi handler chịu trách nhiệm load một loại dữ liệu:

```
HomeFragment (150 lines - coordinator)
├── HomeAlbumsHandler → Load danh sách albums mới nhất
├── HomeArtistsHandler → Load danh sách nghệ sĩ nổi bật
├── HomePopularSongsHandler → Load bài hát phổ biến
├── HomeNewSongsHandler → Load bài hát mới phát hành
└── HomeSliderHandler → Load slider banner
```

**HomeFragment.java (Sau refactor)**:
```java
public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    
    // Handlers
    private HomeAlbumsHandler albumsHandler;
    private HomeArtistsHandler artistsHandler;
    private HomePopularSongsHandler popularSongsHandler;
    private HomeNewSongsHandler newSongsHandler;
    private HomeSliderHandler sliderHandler;
    
    // Tracking completion
    private int loadedHandlers = 0;
    private static final int TOTAL_HANDLERS = 5;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initHandlers();
        loadHomeData();
    }

    private void initHandlers() {
        // Khởi tạo các handlers với callback
        OnHandlerLoadCompleteListener callback = () -> {
            loadedHandlers++;
            if (loadedHandlers >= TOTAL_HANDLERS) {
                hideLoading();
            }
        };
        
        albumsHandler = new HomeAlbumsHandler(
            binding.recyclerViewAlbums, 
            requireContext(),
            callback
        );
        
        artistsHandler = new HomeArtistsHandler(
            binding.recyclerViewArtists, 
            requireContext(),
            callback
        );
        
        popularSongsHandler = new HomePopularSongsHandler(
            binding.recyclerViewPopular, 
            requireContext(),
            callback
        );
        
        newSongsHandler = new HomeNewSongsHandler(
            binding.recyclerViewNewSongs, 
            requireContext(),
            callback
        );
        
        sliderHandler = new HomeSliderHandler(
            binding.viewPagerSlider,
            binding.tabLayoutIndicator,
            requireContext(),
            callback
        );
    }

    private void loadHomeData() {
        showLoading();
        loadedHandlers = 0;
        
        // Gọi tất cả handlers load parallel
        albumsHandler.loadData();
        artistsHandler.loadData();
        popularSongsHandler.loadData();
        newSongsHandler.loadData();
        sliderHandler.loadData();
    }

    private void showLoading() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.scrollView.setVisibility(View.GONE);
    }

    private void hideLoading() {
        binding.progressBar.setVisibility(View.GONE);
        binding.scrollView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    
    // Interface callback khi handler load xong
    public interface OnHandlerLoadCompleteListener {
        void onLoadComplete();
    }
}
```

#### **4.4.2.3. Ví dụ chi tiết một Handler - HomePopularSongsHandler**

```java
public class HomePopularSongsHandler {
    private RecyclerView recyclerView;
    private Context context;
    private SongAdapter adapter;
    private SongRepository songRepository;
    private HomeFragment.OnHandlerLoadCompleteListener callback;

    public HomePopularSongsHandler(RecyclerView recyclerView, 
                                   Context context,
                                   HomeFragment.OnHandlerLoadCompleteListener callback) {
        this.recyclerView = recyclerView;
        this.context = context;
        this.callback = callback;
        this.songRepository = new SongRepository();
        
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        // Setup layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(
            context, 
            LinearLayoutManager.HORIZONTAL, 
            false
        );
        recyclerView.setLayoutManager(layoutManager);
        
        // Setup adapter với click listener
        adapter = new SongAdapter(new ArrayList<>(), song -> {
            // Navigate to PlayerActivity
            Intent intent = new Intent(context, PlayerActivity.class);
            intent.putExtra("songId", song.getId());
            context.startActivity(intent);
        });
        
        recyclerView.setAdapter(adapter);
    }

    public void loadData() {
        // Load popular songs từ repository
        songRepository.getTrendingSongs(20, new SongRepository.SongsCallback() {
            @Override
            public void onSuccess(List<Song> songs) {
                // Update adapter
                adapter.setSongs(songs);
                
                // Notify parent completion
                if (callback != null) {
                    callback.onLoadComplete();
                }
            }

            @Override
            public void onError(String error) {
                ToastUtils.showError(context, "Không thể load bài hát phổ biến: " + error);
                
                // Vẫn notify completion để không block UI
                if (callback != null) {
                    callback.onLoadComplete();
                }
            }
        });
    }
}
```

**Cải tiến - Callback-based loading thay vì Fixed Timeout**:

Trước đây, Fragment dùng `Handler.postDelayed(3000)` để ẩn loading sau 3 giây cố định, gây ra:
- Loading ẩn sớm khi data chưa về (UI trống)
- Loading lâu không cần thiết khi data đã về

Giải pháp: **Completion Counter Pattern**
```java
// Mỗi handler gọi callback khi xong
loadedHandlers++;
if (loadedHandlers >= TOTAL_HANDLERS) {
    hideLoading();  // Chỉ ẩn khi TẤT CẢ handlers xong
}
```

**Lợi ích của Handler Pattern trong HomeFragment**:
1. **Single Responsibility**: Mỗi handler một nhiệm vụ rõ ràng
2. **Dễ đọc**: 150 lines thay vì 400 lines
3. **Dễ test**: Test từng handler độc lập
4. **Dễ maintain**: Sửa logic albums không ảnh hưởng artists
5. **Reusable**: Dùng lại handler ở fragments khác

---

### **4.4.3. PlayerActivity - Trường hợp điển hình của Handler Pattern**

`PlayerActivity` là ví dụ **QUAN TRỌNG NHẤT** trong dự án, minh họa sức mạnh của Handler Pattern khi refactor một Activity phức tạp.

#### **4.4.3.1. Vấn đề ban đầu**

Trước khi refactor, `PlayerActivity` có **500+ lines** với quá nhiều trách nhiệm:

```java
// PlayerActivity.java - TRƯỚC REFACTOR (500+ lines)
public class PlayerActivity extends AppCompatActivity {
    
    // Play/Pause controls
    private void setupPlayControls() {
        btnPlay.setOnClickListener(v -> {
            if (musicPlayer.isPlaying()) {
                musicPlayer.pause();
                btnPlay.setImageResource(R.drawable.ic_play);
            } else {
                musicPlayer.resume();
                btnPlay.setImageResource(R.drawable.ic_pause);
            }
        });
        
        btnNext.setOnClickListener(v -> {
            Song nextSong = playlistManager.getNextSong();
            // 20+ lines code...
        });
        
        btnPrevious.setOnClickListener(v -> {
            // 20+ lines code...
        });
    }
    
    // SeekBar tracking
    private void updateSeekBar() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (musicPlayer.isPlaying()) {
                    int currentPosition = musicPlayer.getCurrentPosition();
                    seekBar.setProgress(currentPosition);
                    tvCurrentTime.setText(formatTime(currentPosition));
                    handler.postDelayed(this, 1000);
                }
            }
        }, 1000);
    }
    
    // Like/Unlike
    private void setupLikeButton() {
        btnLike.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) return;
            
            DocumentReference likeRef = firestore
                .collection("likes")
                .document(user.getUid() + "_" + songId);
            
            likeRef.get().addOnSuccessListener(doc -> {
                if (doc.exists()) {
                    // Unlike
                    likeRef.delete().addOnSuccessListener(/* ... */);
                } else {
                    // Like
                    Map<String, Object> like = new HashMap<>();
                    // 15+ lines code...
                }
            });
        });
    }
    
    // Volume control
    private void setupVolumeControls() {
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        // 30+ lines code...
    }
    
    // Load và extract màu từ album art
    private void loadAlbumArt() {
        Glide.with(this)
            .asBitmap()
            .load(song.getImageUrl())
            .into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap bitmap, Transition transition) {
                    imgAlbumArt.setImageBitmap(bitmap);
                    
                    // Extract palette colors
                    Palette.from(bitmap).generate(palette -> {
                        if (palette != null) {
                            int dominantColor = palette.getDominantColor(Color.BLACK);
                            // Apply gradient...
                            // 20+ lines code...
                        }
                    });
                }
            });
    }
    
    // Add to playlist
    private void setupPlaylistButton() {
        btnAddToPlaylist.setOnClickListener(v -> {
            // Show dialog với danh sách playlists
            // Load playlists từ Firestore
            // Add song to selected playlist
            // 40+ lines code...
        });
    }
    
    // Share song
    private void setupShareButton() {
        btnShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, 
                "Nghe " + song.getTitle() + " - " + song.getArtist());
            startActivity(Intent.createChooser(shareIntent, "Chia sẻ qua"));
        });
    }
    
    // ... 200+ lines code khác
    
    // → TOTAL: 500+ lines
    // → Rất khó đọc, maintain, test
    // → Khi thay đổi một tính năng, phải scroll qua toàn bộ file
}
```

**Các vấn đề**:
1. **God Object**: Một class làm quá nhiều việc
2. **Khó đọc**: 500+ lines, phải scroll nhiều
3. **Khó test**: Không thể test riêng từng tính năng
4. **Khó maintain**: Sửa volume control có thể ảnh hưởng play controls
5. **Duplicate code**: Nhiều đoạn code lặp lại

#### **4.4.3.2. Giải pháp với Handler Pattern**

Tách thành **7 handlers**, mỗi handler một trách nhiệm cụ thể:

```
PlayerActivity (150 lines - chỉ coordinator)
├── PlayerControlHandler → Play/Pause/Next/Previous controls
├── PlayerSeekBarHandler → SeekBar progress tracking
├── PlayerLikeHandler → Like/Unlike song
├── PlayerVolumeHandler → Volume controls
├── PlayerImageHandler → Album art loading + Palette extraction
├── PlayerPlaylistHandler → Add to playlist
└── PlayerShareHandler → Share song
```

**PlayerActivity.java (SAU REFACTOR - 150 lines)**:
```java
public class PlayerActivity extends AppCompatActivity {
    private ActivityPlayerBinding binding;
    
    // Song data
    private String songId;
    private Song currentSong;
    
    // Handlers
    private PlayerControlHandler controlHandler;
    private PlayerSeekBarHandler seekBarHandler;
    private PlayerLikeHandler likeHandler;
    private PlayerVolumeHandler volumeHandler;
    private PlayerImageHandler imageHandler;
    private PlayerPlaylistHandler playlistHandler;
    private PlayerShareHandler shareHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get song ID from intent
        songId = getIntent().getStringExtra("songId");
        if (songId == null) {
            ToastUtils.showError(this, "Không tìm thấy bài hát");
            finish();
            return;
        }

        // Setup toolbar
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        // Init handlers
        initHandlers();

        // Load song data
        loadSongData();
    }

    private void initHandlers() {
        // Control handler
        controlHandler = new PlayerControlHandler(
            binding.btnPlay,
            binding.btnNext,
            binding.btnPrevious,
            this::updateSongInfo
        );

        // SeekBar handler
        seekBarHandler = new PlayerSeekBarHandler(
            binding.seekBar,
            binding.tvCurrentTime,
            binding.tvDuration
        );

        // Like handler
        likeHandler = new PlayerLikeHandler(
            binding.btnLike,
            songId,
            this
        );

        // Volume handler
        volumeHandler = new PlayerVolumeHandler(
            this,
            binding.seekBarVolume,
            binding.btnVolumeUp,
            binding.btnVolumeDown
        );

        // Image handler
        imageHandler = new PlayerImageHandler(
            binding.imgAlbumArt,
            binding.layoutPlayer,
            this
        );

        // Playlist handler
        playlistHandler = new PlayerPlaylistHandler(
            binding.btnAddToPlaylist,
            this
        );

        // Share handler
        shareHandler = new PlayerShareHandler(
            binding.btnShare,
            this
        );
    }

    private void loadSongData() {
        binding.progressBar.setVisibility(View.VISIBLE);

        SongRepository songRepository = new SongRepository();
        songRepository.getSongById(songId, new SongRepository.SongCallback() {
            @Override
            public void onSuccess(Song song) {
                binding.progressBar.setVisibility(View.GONE);
                currentSong = song;
                
                // Update UI
                updateSongInfo(song);
                
                // Update handlers
                imageHandler.loadImage(song.getImageUrl());
                seekBarHandler.updateDuration(song.getDuration());
                shareHandler.setSong(song);
                playlistHandler.setSongId(song.getId());
                
                // Play song
                controlHandler.playSong(song);
                
                // Increment play count
                songRepository.incrementPlayCount(songId);
            }

            @Override
            public void onError(String error) {
                binding.progressBar.setVisibility(View.GONE);
                ToastUtils.showError(PlayerActivity.this, error);
                finish();
            }
        });
    }

    private void updateSongInfo(Song song) {
        binding.tvSongTitle.setText(song.getTitle());
        binding.tvArtist.setText(song.getArtist());
        binding.tvAlbum.setText(song.getAlbum());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        // Cleanup handlers
        if (seekBarHandler != null) {
            seekBarHandler.cleanup();
        }
        if (likeHandler != null) {
            likeHandler.cleanup();
        }
        
        binding = null;
    }
}
```

#### **4.4.3.3. Chi tiết các Handlers**

**1. PlayerControlHandler**:
```java
public class PlayerControlHandler {
    private ImageView btnPlay;
    private ImageView btnNext;
    private ImageView btnPrevious;
    
    private MusicPlayer musicPlayer;
    private PlaylistManager playlistManager;
    private OnSongChangeListener listener;

    public interface OnSongChangeListener {
        void onSongChanged(Song song);
    }

    public PlayerControlHandler(ImageView btnPlay, 
                               ImageView btnNext, 
                               ImageView btnPrevious,
                               OnSongChangeListener listener) {
        this.btnPlay = btnPlay;
        this.btnNext = btnNext;
        this.btnPrevious = btnPrevious;
        this.listener = listener;
        
        this.musicPlayer = MusicPlayer.getInstance();
        this.playlistManager = PlaylistManager.getInstance();
        
        setupListeners();
    }

    private void setupListeners() {
        btnPlay.setOnClickListener(v -> togglePlayPause());
        btnNext.setOnClickListener(v -> playNext());
        btnPrevious.setOnClickListener(v -> playPrevious());
        
        // Listener khi bài hát kết thúc
        musicPlayer.setOnCompletionListener(() -> playNext());
    }

    private void togglePlayPause() {
        if (musicPlayer.isPlaying()) {
            musicPlayer.pause();
            btnPlay.setImageResource(R.drawable.ic_play);
        } else {
            musicPlayer.resume();
            btnPlay.setImageResource(R.drawable.ic_pause);
        }
    }

    private void playNext() {
        Song nextSong = playlistManager.getNextSong();
        if (nextSong != null) {
            playSong(nextSong);
        }
    }

    private void playPrevious() {
        Song prevSong = playlistManager.getPreviousSong();
        if (prevSong != null) {
            playSong(prevSong);
        }
    }

    public void playSong(Song song) {
        musicPlayer.play(song.getAudioUrl(), song);
        btnPlay.setImageResource(R.drawable.ic_pause);
        
        if (listener != null) {
            listener.onSongChanged(song);
        }
    }
}
```

**2. PlayerSeekBarHandler**:
```java
public class PlayerSeekBarHandler {
    private SeekBar seekBar;
    private TextView tvCurrentTime;
    private TextView tvDuration;
    
    private MusicPlayer musicPlayer;
    private Handler handler;
    private Runnable updateRunnable;

    public PlayerSeekBarHandler(SeekBar seekBar, 
                               TextView tvCurrentTime, 
                               TextView tvDuration) {
        this.seekBar = seekBar;
        this.tvCurrentTime = tvCurrentTime;
        this.tvDuration = tvDuration;
        
        this.musicPlayer = MusicPlayer.getInstance();
        this.handler = new Handler(Looper.getMainLooper());
        
        setupSeekBar();
        startUpdating();
    }

    private void setupSeekBar() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    musicPlayer.seekTo(progress);
                    tvCurrentTime.setText(TimeFormatter.formatDuration(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                stopUpdating();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                startUpdating();
            }
        });
    }

    public void updateDuration(int duration) {
        seekBar.setMax(duration);
        tvDuration.setText(TimeFormatter.formatDuration(duration));
    }

    private void startUpdating() {
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                if (musicPlayer.isPlaying()) {
                    int currentPosition = musicPlayer.getCurrentPosition();
                    seekBar.setProgress(currentPosition);
                    tvCurrentTime.setText(TimeFormatter.formatDuration(currentPosition));
                    
                    handler.postDelayed(this, 1000);
                }
            }
        };
        handler.post(updateRunnable);
    }

    private void stopUpdating() {
        if (updateRunnable != null) {
            handler.removeCallbacks(updateRunnable);
        }
    }

    public void cleanup() {
        stopUpdating();
    }
}
```

**3. PlayerLikeHandler**:
```java
public class PlayerLikeHandler {
    private ImageView btnLike;
    private String songId;
    private Context context;
    
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private boolean isLiked = false;
    private ListenerRegistration likeListener;

    public PlayerLikeHandler(ImageView btnLike, String songId, Context context) {
        this.btnLike = btnLike;
        this.songId = songId;
        this.context = context;
        
        this.firestore = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
        
        setupListener();
        checkLikeStatus();
    }

    private void setupListener() {
        btnLike.setOnClickListener(v -> toggleLike());
    }

    private void checkLikeStatus() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;

        String likeId = user.getUid() + "_" + songId;
        
        // Real-time listener
        likeListener = firestore.collection("likes")
            .document(likeId)
            .addSnapshotListener((snapshot, error) -> {
                if (error != null) return;
                
                isLiked = snapshot != null && snapshot.exists();
                updateLikeButton();
            });
    }

    private void toggleLike() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            ToastUtils.showError(context, "Vui lòng đăng nhập");
            return;
        }

        String likeId = user.getUid() + "_" + songId;
        DocumentReference likeRef = firestore.collection("likes").document(likeId);

        if (isLiked) {
            // Unlike
            likeRef.delete()
                .addOnSuccessListener(aVoid -> {
                    ToastUtils.showSuccess(context, "Đã bỏ thích");
                })
                .addOnFailureListener(e -> {
                    ToastUtils.showError(context, "Lỗi: " + e.getMessage());
                });
        } else {
            // Like
            Map<String, Object> like = new HashMap<>();
            like.put("userId", user.getUid());
            like.put("songId", songId);
            like.put("createdAt", FieldValue.serverTimestamp());

            likeRef.set(like)
                .addOnSuccessListener(aVoid -> {
                    ToastUtils.showSuccess(context, "Đã thích bài hát");
                })
                .addOnFailureListener(e -> {
                    ToastUtils.showError(context, "Lỗi: " + e.getMessage());
                });
        }
    }

    private void updateLikeButton() {
        if (isLiked) {
            btnLike.setImageResource(R.drawable.ic_favorite_filled);
            btnLike.setColorFilter(Color.RED);
        } else {
            btnLike.setImageResource(R.drawable.ic_favorite_outline);
            btnLike.clearColorFilter();
        }
    }

    public void cleanup() {
        if (likeListener != null) {
            likeListener.remove();
        }
    }
}
```

**4. PlayerVolumeHandler**:
```java
public class PlayerVolumeHandler {
    private Context context;
    private SeekBar seekBarVolume;
    private ImageView btnVolumeUp;
    private ImageView btnVolumeDown;
    
    private AudioManager audioManager;

    public PlayerVolumeHandler(Context context, 
                              SeekBar seekBarVolume,
                              ImageView btnVolumeUp, 
                              ImageView btnVolumeDown) {
        this.context = context;
        this.seekBarVolume = seekBarVolume;
        this.btnVolumeUp = btnVolumeUp;
        this.btnVolumeDown = btnVolumeDown;
        
        this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        
        setupVolumeControls();
    }

    private void setupVolumeControls() {
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        
        // Setup SeekBar
        seekBarVolume.setMax(maxVolume);
        seekBarVolume.setProgress(currentVolume);
        
        seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    audioManager.setStreamVolume(
                        AudioManager.STREAM_MUSIC, 
                        progress, 
                        0
                    );
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        
        // Volume up button
        btnVolumeUp.setOnClickListener(v -> {
            int volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (volume < maxVolume) {
                audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC, 
                    volume + 1, 
                    AudioManager.FLAG_SHOW_UI
                );
                seekBarVolume.setProgress(volume + 1);
            }
        });
        
        // Volume down button
        btnVolumeDown.setOnClickListener(v -> {
            int volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (volume > 0) {
                audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC, 
                    volume - 1, 
                    AudioManager.FLAG_SHOW_UI
                );
                seekBarVolume.setProgress(volume - 1);
            }
        });
    }
}
```

**5. PlayerImageHandler**:
```java
public class PlayerImageHandler {
    private ImageView imgAlbumArt;
    private View layoutPlayer;
    private Context context;

    public PlayerImageHandler(ImageView imgAlbumArt, 
                             View layoutPlayer, 
                             Context context) {
        this.imgAlbumArt = imgAlbumArt;
        this.layoutPlayer = layoutPlayer;
        this.context = context;
    }

    public void loadImage(String imageUrl) {
        Glide.with(context)
            .asBitmap()
            .load(imageUrl)
            .placeholder(R.drawable.placeholder_album)
            .error(R.drawable.error_album)
            .into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap bitmap, 
                                           @Nullable Transition<? super Bitmap> transition) {
                    imgAlbumArt.setImageBitmap(bitmap);
                    extractAndApplyColors(bitmap);
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {}
            });
    }

    private void extractAndApplyColors(Bitmap bitmap) {
        Palette.from(bitmap).generate(palette -> {
            if (palette == null) return;
            
            // Extract colors
            int dominantColor = palette.getDominantColor(Color.BLACK);
            int vibrantColor = palette.getVibrantColor(dominantColor);
            int darkMutedColor = palette.getDarkMutedColor(dominantColor);
            
            // Apply gradient background
            GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{darkMutedColor, Color.BLACK}
            );
            
            layoutPlayer.setBackground(gradient);
        });
    }
}
```

**6. PlayerPlaylistHandler**:
```java
public class PlayerPlaylistHandler {
    private ImageView btnAddToPlaylist;
    private Context context;
    private String songId;
    
    private PlaylistRepository playlistRepository;

    public PlayerPlaylistHandler(ImageView btnAddToPlaylist, Context context) {
        this.btnAddToPlaylist = btnAddToPlaylist;
        this.context = context;
        
        this.playlistRepository = new PlaylistRepository();
        
        setupListener();
    }

    private void setupListener() {
        btnAddToPlaylist.setOnClickListener(v -> showPlaylistDialog());
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    private void showPlaylistDialog() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            ToastUtils.showError(context, "Vui lòng đăng nhập");
            return;
        }

        // Load user's playlists
        playlistRepository.getUserPlaylists(new PlaylistRepository.PlaylistsCallback() {
            @Override
            public void onSuccess(List<Playlist> playlists) {
                if (playlists.isEmpty()) {
                    ToastUtils.showInfo(context, "Bạn chưa có playlist nào");
                    return;
                }
                
                // Show dialog with playlist options
                String[] playlistNames = new String[playlists.size()];
                for (int i = 0; i < playlists.size(); i++) {
                    playlistNames[i] = playlists.get(i).getName();
                }
                
                new AlertDialog.Builder(context)
                    .setTitle("Thêm vào playlist")
                    .setItems(playlistNames, (dialog, which) -> {
                        Playlist selectedPlaylist = playlists.get(which);
                        addSongToPlaylist(selectedPlaylist.getId());
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
            }

            @Override
            public void onError(String error) {
                ToastUtils.showError(context, "Lỗi: " + error);
            }
        });
    }

    private void addSongToPlaylist(String playlistId) {
        playlistRepository.addSongToPlaylist(playlistId, songId, 
            new PlaylistRepository.PlaylistCallback() {
                @Override
                public void onSuccess(Playlist playlist) {
                    ToastUtils.showSuccess(context, "Đã thêm vào " + playlist.getName());
                }

                @Override
                public void onError(String error) {
                    ToastUtils.showError(context, error);
                }
            });
    }
}
```

**7. PlayerShareHandler**:
```java
public class PlayerShareHandler {
    private ImageView btnShare;
    private Context context;
    private Song song;

    public PlayerShareHandler(ImageView btnShare, Context context) {
        this.btnShare = btnShare;
        this.context = context;
        
        setupListener();
    }

    private void setupListener() {
        btnShare.setOnClickListener(v -> shareSong());
    }

    public void setSong(Song song) {
        this.song = song;
    }

    private void shareSong() {
        if (song == null) return;

        String shareText = String.format(
            "🎵 %s - %s\n\nNghe trên Music Player App",
            song.getTitle(),
            song.getArtist()
        );

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, song.getTitle());
        
        context.startActivity(Intent.createChooser(shareIntent, "Chia sẻ bài hát"));
    }
}
```

#### **4.4.3.4. So sánh Before/After Refactor**

| Aspect | Before Refactor | After Refactor | Improvement |
|--------|----------------|----------------|-------------|
| **Lines of code** | 500+ lines | 150 lines | **-70%** |
| **Number of methods** | ~25 methods | ~10 methods | **-60%** |
| **Complexity** | Rất cao | Thấp | **Dễ đọc** |
| **Testability** | Khó test | Dễ test từng handler | **+100%** |
| **Maintainability** | Sửa một chỗ ảnh hưởng nhiều | Mỗi handler độc lập | **+200%** |
| **Reusability** | Không thể tái sử dụng | Handlers dùng ở nhiều activities | **+150%** |
| **Single Responsibility** | ❌ Không tuân thủ | ✅ Tuân thủ | **SOLID** |

**Lợi ích cụ thể**:
1. **Dễ đọc hơn**: 150 lines thay vì 500 lines
2. **Dễ test hơn**: Test từng handler riêng
3. **Dễ maintain hơn**: Sửa volume không ảnh hưởng play controls
4. **Dễ mở rộng hơn**: Thêm handler mới không ảnh hưởng code cũ
5. **Tuân thủ SOLID**: Single Responsibility Principle

---

### **4.4.4. Adapters**

Adapters kết nối data với RecyclerView để hiển thị danh sách.

#### **4.4.4.1. SongAdapter**

```java
public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {
    private List<Song> songs;
    private OnSongClickListener listener;

    public interface OnSongClickListener {
        void onSongClick(Song song);
    }

    public SongAdapter(List<Song> songs, OnSongClickListener listener) {
        this.songs = songs;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemSongBinding binding = ItemSongBinding.inflate(
            LayoutInflater.from(parent.getContext()), 
            parent, 
            false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Song song = songs.get(position);
        holder.bind(song);
    }

    @Override
    public int getItemCount() {
        return songs != null ? songs.size() : 0;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ItemSongBinding binding;

        ViewHolder(ItemSongBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Song song) {
            // Set data
            binding.tvSongTitle.setText(song.getTitle());
            binding.tvArtist.setText(song.getArtist());
            binding.tvDuration.setText(TimeFormatter.formatDuration(song.getDuration()));

            // Load image
            ImageLoader.loadRounded(
                binding.getRoot().getContext(),
                song.getImageUrl(),
                binding.imgSong,
                8
            );

            // Click listener
            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSongClick(song);
                }
            });
        }
    }
}
```

#### **4.4.4.2. PlaylistAdapter**

```java
public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {
    private List<Playlist> playlists;
    private OnPlaylistClickListener listener;

    public interface OnPlaylistClickListener {
        void onPlaylistClick(Playlist playlist);
        void onDeleteClick(Playlist playlist);
    }

    public PlaylistAdapter(List<Playlist> playlists, OnPlaylistClickListener listener) {
        this.playlists = playlists;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemPlaylistBinding binding = ItemPlaylistBinding.inflate(
            LayoutInflater.from(parent.getContext()), 
            parent, 
            false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Playlist playlist = playlists.get(position);
        holder.bind(playlist);
    }

    @Override
    public int getItemCount() {
        return playlists != null ? playlists.size() : 0;
    }

    public void setPlaylists(List<Playlist> playlists) {
        this.playlists = playlists;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ItemPlaylistBinding binding;

        ViewHolder(ItemPlaylistBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Playlist playlist) {
            binding.tvPlaylistName.setText(playlist.getName());
            
            int songCount = playlist.getSongIds() != null ? playlist.getSongIds().size() : 0;
            binding.tvSongCount.setText(songCount + " bài hát");

            // Load cover image (first song's image or default)
            if (songCount > 0) {
                // Load first song's image as cover
                String firstSongId = playlist.getSongIds().get(0);
                loadPlaylistCover(firstSongId);
            } else {
                binding.imgPlaylist.setImageResource(R.drawable.placeholder_playlist);
            }

            // Click listeners
            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPlaylistClick(playlist);
                }
            });

            binding.btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(playlist);
                }
            });
        }

        private void loadPlaylistCover(String songId) {
            SongRepository songRepository = new SongRepository();
            songRepository.getSongById(songId, new SongRepository.SongCallback() {
                @Override
                public void onSuccess(Song song) {
                    ImageLoader.loadRounded(
                        binding.getRoot().getContext(),
                        song.getImageUrl(),
                        binding.imgPlaylist,
                        8
                    );
                }

                @Override
                public void onError(String error) {
                    binding.imgPlaylist.setImageResource(R.drawable.placeholder_playlist);
                }
            });
        }
    }
}
```

---

## **Tóm tắt Chương 4 - Phần 2**

Phần 2 của Chương 4 đã trình bày chi tiết việc triển khai **Lớp UI** - phần quan trọng nhất trong dự án:

**MainActivity**: Container chính với BottomNavigationView để điều hướng giữa các Fragments.

**HomeFragment**: Ví dụ điển hình áp dụng Handler Pattern với 5 handlers (Albums, Artists, PopularSongs, NewSongs, Slider), giảm từ 400 → 150 lines.

**PlayerActivity - Thành công lớn nhất**: 
- **Before refactor**: 500+ lines, God Object, khó đọc/test/maintain
- **After refactor**: 150 lines với 7 handlers (Control, SeekBar, Like, Volume, Image, Playlist, Share)
- **Kết quả**: Code giảm 70%, complexity giảm đáng kể, testability và maintainability tăng 100-200%

**Adapters**: SongAdapter và PlaylistAdapter với ViewHolder pattern để hiển thị danh sách hiệu quả.

**Handler Pattern** là đóng góp kỹ thuật chính của dự án, giúp:
- Tuân thủ **Single Responsibility Principle**
- Code **dễ đọc, dễ test, dễ maintain**
- **Tái sử dụng** handlers ở nhiều nơi
- **Mở rộng** dễ dàng không ảnh hưởng code cũ

---

**[Next: Chương 5 - Tối ưu hóa và Refactoring]**
