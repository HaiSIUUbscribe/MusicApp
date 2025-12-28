# CHƯƠNG 4: TRIỂN KHAI HỆ THỐNG

## **4.1. Môi trường và Cấu hình dự án**

### **4.1.1. Môi trường phát triển**

**Android Studio**:
- **Version**: Hedgehog | 2023.1.1 Patch 2
- **Build**: AI-231.9392.1
- **JRE**: 17.0.7
- **Kotlin**: 1.9.0

**Android SDK**:
- **Platform**: Android 14.0 (API 34)
- **Build Tools**: 34.0.0
- **SDK Tools**: 26.1.1

**Gradle**:
- **Gradle Version**: 8.2
- **Android Gradle Plugin**: 8.2.0

**Firebase**:
- **Console**: console.firebase.google.com
- **Project ID**: music-player-app-xxxxx
- **Region**: us-central

### **4.1.2. Cấu hình build.gradle**

**Project-level build.gradle** (`build.gradle`):
```gradle
buildscript {
    repositories {
        google()
        mavenCentral()
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.google.services) apply false
}
```

**App-level build.gradle** (`app/build.gradle`):
```gradle
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services)
}

android {
    namespace 'com.example.musicapplication'
    compileSdk 36

    defaultConfig {
        applicationId "com.example.musicapplication"
        minSdk 27
        targetSdk 36
        versionCode 1
        versionName "1.0"

        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'),
                         'proguard-rules.pro'
        }
    }
    
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_11
        targetCompatibility JavaVersion.VERSION_11
    }
    
    buildFeatures {
        viewBinding true
        dataBinding true
    }
}

dependencies {
    // Core Android
    implementation libs.appcompat
    implementation libs.material
    implementation libs.activity
    implementation libs.constraintlayout
    implementation libs.cardview
    
    // Firebase BOM - manages all Firebase library versions
    implementation platform(libs.firebase.bom)
    implementation libs.firebase.auth
    implementation libs.firebase.firestore
    implementation libs.firebase.storage
    
    // Image loading
    implementation libs.glide
    annotationProcessor libs.glide.compiler
    
    // Testing
    testImplementation libs.junit
    androidTestImplementation libs.ext.junit
    androidTestImplementation libs.espresso.core
}
```

**libs.versions.toml** (`gradle/libs.versions.toml`):
```toml
[versions]
appcompat = "1.6.1"
material = "1.12.0"
activity = "1.8.0"
constraintlayout = "2.1.4"
cardview = "1.0.0"
firebase-bom = "32.7.0"
glide = "4.16.0"
junit = "4.13.2"

[libraries]
appcompat = { group = "androidx.appcompat", name = "appcompat", version.ref = "appcompat" }
material = { group = "com.google.android.material", name = "material", version.ref = "material" }
activity = { group = "androidx.activity", name = "activity", version.ref = "activity" }
constraintlayout = { group = "androidx.constraintlayout", name = "constraintlayout", version.ref = "constraintlayout" }
cardview = { group = "androidx.cardview", name = "cardview", version.ref = "cardview" }

firebase-bom = { group = "com.google.firebase", name = "firebase-bom", version.ref = "firebase-bom" }
firebase-auth = { group = "com.google.firebase", name = "firebase-auth" }
firebase-firestore = { group = "com.google.firebase", name = "firebase-firestore" }
firebase-storage = { group = "com.google.firebase", name = "firebase-storage" }

glide = { group = "com.github.bumptech.glide", name = "glide", version.ref = "glide" }
glide-compiler = { group = "com.github.bumptech.glide", name = "compiler", version.ref = "glide" }

junit = { group = "junit", name = "junit", version.ref = "junit" }
ext-junit = { group = "androidx.test.ext", name = "junit", version = "1.1.5" }
espresso-core = { group = "androidx.test.espresso", name = "espresso-core", version = "3.5.1" }

[plugins]
android-application = { id = "com.android.application", version = "8.2.0" }
google-services = { id = "com.google.gms.google-services", version = "4.4.0" }
```

### **4.1.3. Cấu hình Firebase**

**Thêm google-services.json**:
1. Tạo project trên Firebase Console
2. Add Android app với package name: `com.example.musicapplication`
3. Download `google-services.json`
4. Copy vào `app/` directory

**AndroidManifest.xml**:
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <!-- Permissions -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
        tools:ignore="ScopedStorage" />

    <application
        android:name=".MusicApplication"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.MusicApplication"
        android:usesCleartextTraffic="true"
        tools:targetApi="31">
        
        <!-- Activities -->
        <activity
            android:name=".ui.activity.auth.LoginActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        
        <activity android:name=".ui.activity.auth.RegisterActivity" />
        <activity android:name=".ui.activity.main.MainActivity" />
        <activity android:name=".ui.activity.player.PlayerActivity" />
        <!-- Other activities... -->
        
    </application>
</manifest>
```

---

## **4.2. Triển khai Data Layer**

### **4.2.1. Model Classes**

**Song.java** - Model chính của ứng dụng:
```java
public class Song implements Serializable {
    private String id;
    private String title;
    private String artist;
    private String album;
    private String genre;
    private long duration;          // milliseconds
    private String audioUrl;
    private String imageUrl;
    private String playCount;       // String type (Firestore issue)
    private Timestamp uploadDate;
    private String uploadedBy;
    private List<String> tags;

    // Constructors
    public Song() {
        // Required for Firestore
    }

    public Song(String title, String artist, String album) {
        this.title = title;
        this.artist = artist;
        this.album = album;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }
    
    public long getDuration() { return duration; }
    public void setDuration(long duration) { this.duration = duration; }
    
    public String getAudioUrl() { return audioUrl; }
    public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    // Helper method - convert String playCount to long
    public long getPlayCountAsLong() {
        try {
            return Long.parseLong(playCount != null ? playCount : "0");
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    public void setPlayCount(String playCount) {
        this.playCount = playCount;
    }
    
    // Formatted duration
    public String getFormattedDuration() {
        return TimeFormatter.formatDuration(duration);
    }
    
    // Formatted play count
    public String getFormattedPlayCount() {
        return TimeFormatter.formatPlayCount(getPlayCountAsLong());
    }
}
```

**Playlist.java**:
```java
public class Playlist implements Serializable {
    private String id;
    private String userId;
    private String name;
    private String description;
    private String imageUrl;
    private List<String> songs;     // List of song IDs
    private String songCount;
    private boolean isPublic;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Playlist() {
        this.songs = new ArrayList<>();
    }

    public Playlist(String name, String userId) {
        this();
        this.name = name;
        this.userId = userId;
        this.createdAt = Timestamp.now();
        this.updatedAt = Timestamp.now();
    }

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public List<String> getSongs() {
        return songs != null ? songs : new ArrayList<>();
    }
    
    public void setSongs(List<String> songs) {
        this.songs = songs;
    }
    
    public int getSongCountAsInt() {
        try {
            return Integer.parseInt(songCount != null ? songCount : "0");
        } catch (NumberFormatException e) {
            return songs != null ? songs.size() : 0;
        }
    }
}
```

**User.java**, **Album.java**, **Artist.java**, **History.java** có cấu trúc tương tự.

### **4.2.2. Repository Pattern Implementation**

**SongRepository.java** - Repository quan trọng nhất:

```java
public class SongRepository {
    private static final String TAG = "SongRepository";
    private final FirebaseFirestore firestore;
    private final Context context;

    public SongRepository(Context context) {
        this.context = context;
        this.firestore = FirebaseFirestore.getInstance();
    }

    // Callback interface
    public interface SongsCallback {
        void onSuccess(List<Song> songs);
        void onError(String error);
    }

    public interface SongCallback {
        void onSuccess(Song song);
        void onError(String error);
    }

    /**
     * Lấy danh sách trending songs (sắp xếp theo playCount)
     */
    public void getTrendingSongs(int limit, SongsCallback callback) {
        // Check network trước
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
                        if (song != null) {
                            song.setId(doc.getId());
                            songs.add(song);
                        }
                    }
                    callback.onSuccess(songs);
                })
                .addOnFailureListener(e -> {
                    Logger.e(TAG, "Error getting trending songs", e);
                    callback.onError(e.getMessage());
                });
    }

    /**
     * Lấy bài hát mới upload gần đây
     */
    public void getRecentlyAddedSongs(int limit, SongsCallback callback) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            callback.onError("Không có kết nối mạng");
            return;
        }

        firestore.collection("songs")
                .orderBy("uploadDate", Query.Direction.DESCENDING)
                .limit(limit)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Song> songs = convertToSongList(querySnapshot);
                    callback.onSuccess(songs);
                })
                .addOnFailureListener(e -> {
                    Logger.e(TAG, "Error getting recent songs", e);
                    callback.onError(e.getMessage());
                });
    }

    /**
     * Lấy bài hát theo nghệ sĩ
     * Note: Cần composite index cho query này
     */
    public void getSongsByArtist(String artist, SongsCallback callback) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            callback.onError("Không có kết nối mạng");
            return;
        }

        firestore.collection("songs")
                .whereEqualTo("artist", artist)
                // .orderBy("playCount", Query.Direction.DESCENDING) 
                // Commented vì cần composite index
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Song> songs = convertToSongList(querySnapshot);
                    callback.onSuccess(songs);
                })
                .addOnFailureListener(e -> {
                    Logger.e(TAG, "Error getting artist songs", e);
                    callback.onError(e.getMessage());
                });
    }

    /**
     * Tìm kiếm bài hát theo query
     * Note: Firestore không hỗ trợ full-text search, 
     * cần filter client-side hoặc dùng Algolia
     */
    public void searchSongs(String query, SongsCallback callback) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            callback.onError("Không có kết nối mạng");
            return;
        }

        firestore.collection("songs")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Song> allSongs = convertToSongList(querySnapshot);
                    
                    // Filter client-side
                    List<Song> results = new ArrayList<>();
                    String lowerQuery = query.toLowerCase();
                    
                    for (Song song : allSongs) {
                        if (song.getTitle().toLowerCase().contains(lowerQuery) ||
                            song.getArtist().toLowerCase().contains(lowerQuery) ||
                            song.getAlbum().toLowerCase().contains(lowerQuery)) {
                            results.add(song);
                        }
                    }
                    
                    callback.onSuccess(results);
                })
                .addOnFailureListener(e -> {
                    Logger.e(TAG, "Error searching songs", e);
                    callback.onError(e.getMessage());
                });
    }

    /**
     * Lấy thông tin một bài hát
     */
    public void getSongById(String songId, SongCallback callback) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            callback.onError("Không có kết nối mạng");
            return;
        }

        firestore.collection("songs")
                .document(songId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        Song song = document.toObject(Song.class);
                        if (song != null) {
                            song.setId(document.getId());
                            callback.onSuccess(song);
                        } else {
                            callback.onError("Không thể parse dữ liệu");
                        }
                    } else {
                        callback.onError("Bài hát không tồn tại");
                    }
                })
                .addOnFailureListener(e -> {
                    Logger.e(TAG, "Error getting song", e);
                    callback.onError(e.getMessage());
                });
    }

    /**
     * Lấy nhiều bài hát theo IDs
     */
    public void getSongsByIds(List<String> songIds, SongsCallback callback) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            callback.onError("Không có kết nối mạng");
            return;
        }

        if (songIds == null || songIds.isEmpty()) {
            callback.onSuccess(new ArrayList<>());
            return;
        }

        // Firestore giới hạn 10 IDs per query
        if (songIds.size() > 10) {
            // Split thành multiple queries
            List<Song> allSongs = new ArrayList<>();
            int[] completed = {0};
            int batches = (int) Math.ceil(songIds.size() / 10.0);
            
            for (int i = 0; i < songIds.size(); i += 10) {
                int end = Math.min(i + 10, songIds.size());
                List<String> batch = songIds.subList(i, end);
                
                firestore.collection("songs")
                        .whereIn(FieldPath.documentId(), batch)
                        .get()
                        .addOnSuccessListener(querySnapshot -> {
                            allSongs.addAll(convertToSongList(querySnapshot));
                            completed[0]++;
                            
                            if (completed[0] == batches) {
                                callback.onSuccess(allSongs);
                            }
                        })
                        .addOnFailureListener(e -> {
                            Logger.e(TAG, "Error in batch query", e);
                            callback.onError(e.getMessage());
                        });
            }
        } else {
            firestore.collection("songs")
                    .whereIn(FieldPath.documentId(), songIds)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        List<Song> songs = convertToSongList(querySnapshot);
                        callback.onSuccess(songs);
                    })
                    .addOnFailureListener(e -> {
                        Logger.e(TAG, "Error getting songs by IDs", e);
                        callback.onError(e.getMessage());
                    });
        }
    }

    /**
     * Helper method: Convert QuerySnapshot to List<Song>
     */
    private List<Song> convertToSongList(QuerySnapshot querySnapshot) {
        List<Song> songs = new ArrayList<>();
        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
            Song song = doc.toObject(Song.class);
            if (song != null) {
                song.setId(doc.getId());
                songs.add(song);
            }
        }
        return songs;
    }

    /**
     * Tăng play count
     */
    public void incrementPlayCount(String songId) {
        firestore.collection("songs")
                .document(songId)
                .update("playCount", FieldValue.increment(1))
                .addOnSuccessListener(aVoid -> {
                    Logger.d(TAG, "Play count incremented for " + songId);
                })
                .addOnFailureListener(e -> {
                    Logger.e(TAG, "Failed to increment play count", e);
                });
    }
}
```

**PlaylistRepository.java** - Quản lý playlists:

```java
public class PlaylistRepository {
    private final FirebaseFirestore firestore;
    private final Context context;

    public interface PlaylistsCallback {
        void onSuccess(List<Playlist> playlists);
        void onError(String error);
    }

    public interface PlaylistCallback {
        void onSuccess(Playlist playlist);
        void onError(String error);
    }

    public interface OperationCallback {
        void onSuccess();
        void onError(String error);
    }

    public PlaylistRepository(Context context) {
        this.context = context;
        this.firestore = FirebaseFirestore.getInstance();
    }

    /**
     * Lấy tất cả playlists của user
     */
    public void getUserPlaylists(String userId, PlaylistsCallback callback) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            callback.onError("Không có kết nối mạng");
            return;
        }

        firestore.collection("playlists")
                .whereEqualTo("userId", userId)
                .orderBy("updatedAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Playlist> playlists = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Playlist playlist = doc.toObject(Playlist.class);
                        if (playlist != null) {
                            playlist.setId(doc.getId());
                            playlists.add(playlist);
                        }
                    }
                    callback.onSuccess(playlists);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    /**
     * Tạo playlist mới
     */
    public void createPlaylist(Playlist playlist, PlaylistCallback callback) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            callback.onError("Không có kết nối mạng");
            return;
        }

        playlist.setCreatedAt(Timestamp.now());
        playlist.setUpdatedAt(Timestamp.now());

        firestore.collection("playlists")
                .add(playlist)
                .addOnSuccessListener(docRef -> {
                    playlist.setId(docRef.getId());
                    callback.onSuccess(playlist);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    /**
     * Thêm bài hát vào playlist
     */
    public void addSongToPlaylist(String playlistId, String songId, 
                                   OperationCallback callback) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            callback.onError("Không có kết nối mạng");
            return;
        }

        DocumentReference playlistRef = firestore.collection("playlists")
                                                  .document(playlistId);

        firestore.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(playlistRef);
            Playlist playlist = snapshot.toObject(Playlist.class);
            
            if (playlist != null) {
                List<String> songs = playlist.getSongs();
                if (!songs.contains(songId)) {
                    songs.add(songId);
                    transaction.update(playlistRef, "songs", songs);
                    transaction.update(playlistRef, "songCount", 
                                     String.valueOf(songs.size()));
                    transaction.update(playlistRef, "updatedAt", 
                                     Timestamp.now());
                }
            }
            return null;
        })
        .addOnSuccessListener(aVoid -> callback.onSuccess())
        .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    /**
     * Xóa bài hát khỏi playlist
     */
    public void removeSongFromPlaylist(String playlistId, String songId,
                                        OperationCallback callback) {
        // Tương tự addSongToPlaylist, nhưng remove thay vì add
    }

    /**
     * Xóa playlist
     */
    public void deletePlaylist(String playlistId, OperationCallback callback) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            callback.onError("Không có kết nối mạng");
            return;
        }

        firestore.collection("playlists")
                .document(playlistId)
                .delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }
}
```

**FavoriteRepository.java**, **HistoryRepository.java**, **AuthRepository.java** có implementation tương tự với các CRUD operations cho từng entity.

### **4.2.3. Network Error Handling**

**NetworkUtils.java**:
```java
public class NetworkUtils {
    public static boolean isNetworkAvailable(Context context) {
        if (context == null) return false;
        
        ConnectivityManager cm = (ConnectivityManager) 
            context.getSystemService(Context.CONNECTIVITY_SERVICE);
        
        if (cm == null) return false;
        
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static String getNetworkType(Context context) {
        if (!isNetworkAvailable(context)) return "No Connection";
        
        ConnectivityManager cm = (ConnectivityManager) 
            context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        
        if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
            return "WiFi";
        } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
            return "Mobile Data";
        }
        return "Unknown";
    }
}
```

**Sử dụng trong Repository**:
```java
// Luôn check network trước khi query Firestore
if (!NetworkUtils.isNetworkAvailable(context)) {
    callback.onError("Không có kết nối mạng");
    return;
}
```

---

## **4.3. Triển khai Business Logic Layer**

### **4.3.1. MusicPlayer Singleton**

```java
public class MusicPlayer {
    private static MusicPlayer instance;
    private MediaPlayer mediaPlayer;
    private Song currentSong;
    private boolean isPlaying = false;
    private List<OnCompletionListener> completionListeners;
    
    private MusicPlayer() {
        mediaPlayer = new MediaPlayer();
        completionListeners = new ArrayList<>();
        
        mediaPlayer.setOnCompletionListener(mp -> {
            isPlaying = false;
            notifyCompletion();
        });
        
        mediaPlayer.setOnErrorListener((mp, what, extra) -> {
            Logger.e("MusicPlayer", "Error: " + what + ", " + extra);
            return false;
        });
    }
    
    public static synchronized MusicPlayer getInstance() {
        if (instance == null) {
            instance = new MusicPlayer();
        }
        return instance;
    }
    
    public void play(String audioUrl, Song song) {
        try {
            if (isPlaying) {
                stop();
            }
            
            currentSong = song;
            mediaPlayer.reset();
            mediaPlayer.setDataSource(audioUrl);
            mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            );
            
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                isPlaying = true;
            });
            
        } catch (IOException e) {
            Logger.e("MusicPlayer", "Error playing audio", e);
        }
    }
    
    public void pause() {
        if (isPlaying && mediaPlayer != null) {
            mediaPlayer.pause();
            isPlaying = false;
        }
    }
    
    public void resume() {
        if (!isPlaying && mediaPlayer != null) {
            mediaPlayer.start();
            isPlaying = true;
        }
    }
    
    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            isPlaying = false;
        }
    }
    
    public void seekTo(int position) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(position);
        }
    }
    
    public int getCurrentPosition() {
        return mediaPlayer != null ? mediaPlayer.getCurrentPosition() : 0;
    }
    
    public int getDuration() {
        return mediaPlayer != null ? mediaPlayer.getDuration() : 0;
    }
    
    public boolean isPlaying() {
        return isPlaying;
    }
    
    public Song getCurrentSong() {
        return currentSong;
    }
    
    // Listener pattern for completion
    public interface OnCompletionListener {
        void onCompletion();
    }
    
    public void addCompletionListener(OnCompletionListener listener) {
        completionListeners.add(listener);
    }
    
    public void removeCompletionListener(OnCompletionListener listener) {
        completionListeners.remove(listener);
    }
    
    private void notifyCompletion() {
        for (OnCompletionListener listener : completionListeners) {
            listener.onCompletion();
        }
    }
    
    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            isPlaying = false;
        }
    }
}
```

### **4.3.2. PlaylistManager Singleton**

```java
public class PlaylistManager {
    private static PlaylistManager instance;
    private List<Song> playlist;
    private int currentIndex;
    private boolean shuffleMode;
    private RepeatMode repeatMode;
    private List<Song> originalPlaylist;  // For shuffle
    
    public enum RepeatMode {
        OFF, ALL, ONE
    }
    
    private PlaylistManager() {
        playlist = new ArrayList<>();
        originalPlaylist = new ArrayList<>();
        currentIndex = 0;
        shuffleMode = false;
        repeatMode = RepeatMode.OFF;
    }
    
    public static synchronized PlaylistManager getInstance() {
        if (instance == null) {
            instance = new PlaylistManager();
        }
        return instance;
    }
    
    public void setPlaylist(List<Song> songs) {
        this.playlist = new ArrayList<>(songs);
        this.originalPlaylist = new ArrayList<>(songs);
        this.currentIndex = 0;
    }
    
    public void setPlaylist(List<Song> songs, int startIndex) {
        setPlaylist(songs);
        this.currentIndex = startIndex;
    }
    
    public Song getCurrentSong() {
        if (playlist.isEmpty() || currentIndex < 0 || currentIndex >= playlist.size()) {
            return null;
        }
        return playlist.get(currentIndex);
    }
    
    public Song getNextSong() {
        if (playlist.isEmpty()) return null;
        
        if (repeatMode == RepeatMode.ONE) {
            return getCurrentSong();
        }
        
        currentIndex++;
        if (currentIndex >= playlist.size()) {
            if (repeatMode == RepeatMode.ALL) {
                currentIndex = 0;
            } else {
                currentIndex = playlist.size() - 1;
                return null;  // End of playlist
            }
        }
        
        return getCurrentSong();
    }
    
    public Song getPreviousSong() {
        if (playlist.isEmpty()) return null;
        
        currentIndex--;
        if (currentIndex < 0) {
            if (repeatMode == RepeatMode.ALL) {
                currentIndex = playlist.size() - 1;
            } else {
                currentIndex = 0;
                return null;
            }
        }
        
        return getCurrentSong();
    }
    
    public void toggleShuffle() {
        shuffleMode = !shuffleMode;
        
        if (shuffleMode) {
            // Save current song
            Song current = getCurrentSong();
            
            // Shuffle
            Collections.shuffle(playlist);
            
            // Move current song to index 0
            if (current != null) {
                playlist.remove(current);
                playlist.add(0, current);
                currentIndex = 0;
            }
        } else {
            // Restore original order
            Song current = getCurrentSong();
            playlist = new ArrayList<>(originalPlaylist);
            
            // Find index of current song
            if (current != null) {
                currentIndex = playlist.indexOf(current);
            }
        }
    }
    
    public void setRepeatMode(RepeatMode mode) {
        this.repeatMode = mode;
    }
    
    public RepeatMode cycleRepeatMode() {
        switch (repeatMode) {
            case OFF:
                repeatMode = RepeatMode.ALL;
                break;
            case ALL:
                repeatMode = RepeatMode.ONE;
                break;
            case ONE:
                repeatMode = RepeatMode.OFF;
                break;
        }
        return repeatMode;
    }
    
    public boolean isShuffleMode() {
        return shuffleMode;
    }
    
    public RepeatMode getRepeatMode() {
        return repeatMode;
    }
    
    public List<Song> getPlaylist() {
        return new ArrayList<>(playlist);
    }
    
    public int getCurrentIndex() {
        return currentIndex;
    }
    
    public int getPlaylistSize() {
        return playlist.size();
    }
}
```

### **4.3.3. Utility Classes**

**ImageLoader.java** - Glide wrapper:
```java
public class ImageLoader {
    public static void load(Context context, String url, ImageView imageView) {
        Glide.with(context)
             .load(url)
             .placeholder(R.drawable.ic_music)
             .error(R.drawable.ic_music)
             .into(imageView);
    }
    
    public static void loadRounded(Context context, String url, 
                                   ImageView imageView, int radius) {
        Glide.with(context)
             .load(url)
             .placeholder(R.drawable.ic_music)
             .error(R.drawable.ic_music)
             .transform(new CenterCrop(), new RoundedCorners(radius))
             .into(imageView);
    }
    
    public static void loadCircle(Context context, String url, ImageView imageView) {
        Glide.with(context)
             .load(url)
             .placeholder(R.drawable.ic_person)
             .error(R.drawable.ic_person)
             .transform(new CircleCrop())
             .into(imageView);
    }
    
    public static void loadWithCallback(Context context, String url, 
                                       ImageView imageView, 
                                       BitmapCallback callback) {
        Glide.with(context)
             .asBitmap()
             .load(url)
             .into(new CustomTarget<Bitmap>() {
                 @Override
                 public void onResourceReady(@NonNull Bitmap bitmap,
                                            @Nullable Transition transition) {
                     imageView.setImageBitmap(bitmap);
                     if (callback != null) {
                         callback.onBitmapLoaded(bitmap);
                     }
                 }
                 
                 @Override
                 public void onLoadCleared(@Nullable Drawable placeholder) {}
             });
    }
    
    public interface BitmapCallback {
        void onBitmapLoaded(Bitmap bitmap);
    }
}
```

**TimeFormatter.java**:
```java
public class TimeFormatter {
    public static String formatDuration(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }
    
    public static String formatPlayCount(long count) {
        if (count < 1000) {
            return String.valueOf(count);
        } else if (count < 1000000) {
            return String.format(Locale.US, "%.1fK", count / 1000.0);
        } else {
            return String.format(Locale.US, "%.1fM", count / 1000000.0);
        }
    }
    
    public static String formatTimeAgo(Timestamp timestamp) {
        if (timestamp == null) return "";
        
        long now = System.currentTimeMillis();
        long time = timestamp.toDate().getTime();
        long diff = now - time;
        
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        
        if (days > 0) {
            return days + " ngày trước";
        } else if (hours > 0) {
            return hours + " giờ trước";
        } else if (minutes > 0) {
            return minutes + " phút trước";
        } else {
            return "Vừa xong";
        }
    }
}
```

**ToastUtils.java**, **ValidationUtils.java** đã được trình bày ở Chương 2.

---

## **Tóm tắt Chương 4 (Phần 1)**

Phần đầu của Chương 4 đã trình bày:

**4.1. Môi trường & Cấu hình**:
- Android Studio setup
- Gradle configuration với version catalog
- Firebase integration
- AndroidManifest.xml

**4.2. Data Layer**:
- Model classes (Song, Playlist với Serializable)
- SongRepository: Trending, recent, search, getById với network check
- PlaylistRepository: CRUD operations với Firestore transactions
- NetworkUtils cho offline handling

**4.3. Business Logic**:
- MusicPlayer Singleton: MediaPlayer wrapper với lifecycle
- PlaylistManager: Shuffle/repeat, next/previous logic
- Utility Classes: ImageLoader (Glide), TimeFormatter

**[Tiếp tục: 4.4. UI Layer - phần quan trọng nhất]**
