package com.example.musicapplication.data.repository;

import android.content.Context;
import android.util.Log;

import com.example.musicapplication.model.Song;
import com.example.musicapplication.utils.NetworkUtils;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository chuyên xử lý các thao tác cơ bản về Song
 * Các chức năng chuyên biệt đã được tách ra:
 * - AlbumRepository: Quản lý albums
 * - SearchRepository: Tìm kiếm bài hát
 * - FavoriteRepository: Quản lý bài hát yêu thích
 * - SongUploadRepository: Upload và quản lý file
 */
public class SongRepository {
    private static final String TAG = "SongRepository";
    private static final String SONGS_COLLECTION = "songs";

    private FirebaseFirestore firestore;
    private Context context;

    public interface OnResultListener<T> {
        void onSuccess(T result);
        void onError(Exception error);
    }

    public SongRepository(Context context) {
        this.context = context.getApplicationContext();
        this.firestore = FirebaseFirestore.getInstance();
    }

    /**
     * Lấy danh sách bài hát theo IDs
     * Note: Method này được giữ lại vì nhiều nơi sử dụng
     */
    public void getSongsByIds(List<String> songIds, OnResultListener<List<Song>> listener) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            listener.onError(new Exception("Không có kết nối mạng"));
            return;
        }
        if (songIds == null || songIds.isEmpty()) {
            listener.onSuccess(new ArrayList<>());
            return;
        }
        // Limit query to 10 items for demo/simplicity
        List<String> chunk = songIds.subList(0, Math.min(songIds.size(), 10));

        firestore.collection(SONGS_COLLECTION)
                .whereIn(com.google.firebase.firestore.FieldPath.documentId(), chunk)
                .get()
                .addOnSuccessListener(snapshots -> {
                    List<Song> songs = new ArrayList<>();
                    for (var doc : snapshots) {
                        Song s = documentToSong(doc.getId(), doc.getData());
                        if (s != null) songs.add(s);
                    }
                    listener.onSuccess(songs);
                })
                .addOnFailureListener(listener::onError);
    }

    /**
     * Lấy bài hát trending (nhiều lượt nghe nhất)
     */
    public void getTrendingSongs(int limit, OnResultListener<List<Song>> listener) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            listener.onError(new Exception("Không có kết nối mạng"));
            return;
        }
        firestore.collection(SONGS_COLLECTION)
                .orderBy("playCount", Query.Direction.DESCENDING)
                .limit(limit)
                .get()
                .addOnSuccessListener(s -> parseSongs(s, listener))
                .addOnFailureListener(listener::onError);
    }

    /**
     * Lấy bài hát mới thêm gần đây
     */
    public void getRecentlyAddedSongs(int limit, OnResultListener<List<Song>> listener) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            listener.onError(new Exception("Không có kết nối mạng"));
            return;
        }
        firestore.collection(SONGS_COLLECTION)
                .orderBy("uploadDate", Query.Direction.DESCENDING)
                .limit(limit)
                .get()
                .addOnSuccessListener(s -> parseSongs(s, listener))
                .addOnFailureListener(listener::onError);
    }

    /**
     * Lấy tất cả bài hát
     */
    public void getAllSongs(OnResultListener<List<Song>> listener) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            listener.onError(new Exception("Không có kết nối mạng"));
            return;
        }
        firestore.collection(SONGS_COLLECTION)
                .orderBy("uploadDate", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(s -> parseSongs(s, listener))
                .addOnFailureListener(listener::onError);
    }

    /**
     * Lấy bài hát theo ID
     */
    public void getSongById(String songId, OnResultListener<Song> listener) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            listener.onError(new Exception("Không có kết nối mạng"));
            return;
        }
        firestore.collection(SONGS_COLLECTION).document(songId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) listener.onSuccess(documentToSong(doc.getId(), doc.getData()));
                    else listener.onError(new Exception("Not found"));
                })
                .addOnFailureListener(listener::onError);
    }

    /**
     * Tăng số lượt nghe của bài hát
     */
    public void incrementPlayCount(String songId) {
        firestore.collection(SONGS_COLLECTION).document(songId)
                .update("playCount", FieldValue.increment(1));
    }

    /**
     * Lấy danh sách bài hát theo tên nghệ sĩ
     */
    public void getSongsByArtist(String artistName, OnResultListener<List<Song>> listener) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            listener.onError(new Exception("Không có kết nối mạng"));
            return;
        }
        if (artistName == null || artistName.trim().isEmpty()) {
            listener.onSuccess(new ArrayList<>());
            return;
        }

        firestore.collection(SONGS_COLLECTION)
                .whereEqualTo("artist", artistName)
                .get()
                .addOnSuccessListener(s -> parseSongs(s, listener))
                .addOnFailureListener(listener::onError);
    }

    // --- HELPERS ---

    /**
     * Helper method: Parse QuerySnapshot to List of Songs
     */
    private void parseSongs(com.google.firebase.firestore.QuerySnapshot s, OnResultListener<List<Song>> l) {
        List<Song> list = new ArrayList<>();
        for (var d : s) {
            Song obj = documentToSong(d.getId(), d.getData());
            if (obj != null) list.add(obj);
        }
        l.onSuccess(list);
    }

    /**
     * Helper method: Convert Firestore document to Song object
     */
    private Song documentToSong(String docId, java.util.Map<String, Object> data) {
        try {
            Song song = new Song();
            song.id = docId;
            song.title = (String) data.get("title");
            song.artist = (String) data.get("artist");
            song.album = (String) data.get("album");
            song.audioUrl = (String) data.get("audioUrl");
            song.imageUrl = (String) data.get("imageUrl");
            Object d = data.get("duration");
            song.duration = d != null ? ((Number) d).longValue() : 0;
            Object pc = data.get("playCount");
            song.playCount = pc != null ? ((Number) pc).intValue() : 0;
            Object tags = data.get("tags");
            song.tags = tags != null ? (List<String>) tags : new ArrayList<>();
            return song;
        } catch (Exception e) {
            Log.e(TAG, "Error parsing song", e);
            return null;
        }
    }
}