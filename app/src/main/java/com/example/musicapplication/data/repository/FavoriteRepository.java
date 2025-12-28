package com.example.musicapplication.data.repository;

import android.content.Context;
import android.util.Log;

import com.example.musicapplication.model.Song;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository chuyên xử lý các thao tác liên quan đến bài hát yêu thích
 */
public class FavoriteRepository {
    private static final String TAG = "FavoriteRepository";
    private static final String SONGS_COLLECTION = "songs";
    private static final String USERS_COLLECTION = "users";

    private FirebaseFirestore firestore;
    private Context context;

    public interface OnResultListener<T> {
        void onSuccess(T result);
        void onError(Exception error);
    }

    public FavoriteRepository(Context context) {
        this.context = context.getApplicationContext();
        this.firestore = FirebaseFirestore.getInstance();
    }

    /**
     * Kiểm tra xem bài hát có được yêu thích hay không
     */
    public void checkIsLiked(String songId, OnResultListener<Boolean> listener) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            listener.onSuccess(false);
            return;
        }

        firestore.collection(USERS_COLLECTION).document(user.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> favoriteSongs = (List<String>) documentSnapshot.get("favoriteSongs");
                        if (favoriteSongs != null && favoriteSongs.contains(songId)) {
                            listener.onSuccess(true);
                        } else {
                            listener.onSuccess(false);
                        }
                    } else {
                        listener.onSuccess(false);
                    }
                })
                .addOnFailureListener(listener::onError);
    }

    /**
     * Lắng nghe thay đổi danh sách bài hát yêu thích (realtime)
     */
    public ListenerRegistration listenToLikedSongs(OnResultListener<List<Song>> listener) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return null;

        return firestore.collection(USERS_COLLECTION).document(user.getUid())
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (e != null) {
                        listener.onError(e);
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        List<String> favoriteIds = (List<String>) documentSnapshot.get("favoriteSongs");
                        if (favoriteIds == null || favoriteIds.isEmpty()) {
                            listener.onSuccess(new ArrayList<>());
                            return;
                        }
                        getSongsByIds(favoriteIds, listener);
                    } else {
                        listener.onSuccess(new ArrayList<>());
                    }
                });
    }

    /**
     * Cập nhật trạng thái yêu thích của bài hát
     */
    public void updateFavorite(String songId, boolean isLiked, OnResultListener<Boolean> listener) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            if (listener != null) listener.onError(new Exception("User not logged in"));
            return;
        }

        FieldValue operation = isLiked ? FieldValue.arrayUnion(songId) : FieldValue.arrayRemove(songId);
        firestore.collection(USERS_COLLECTION).document(user.getUid()).update("favoriteSongs", operation)
                .addOnSuccessListener(v -> {
                    // Cập nhật likeCount trong bài hát
                    firestore.collection(SONGS_COLLECTION).document(songId)
                            .update("likeCount", FieldValue.increment(isLiked ? 1 : -1));
                    if (listener != null) listener.onSuccess(isLiked);
                })
                .addOnFailureListener(e -> {
                    if (listener != null) listener.onError(e);
                });
    }

    /**
     * Thêm bài hát vào danh sách yêu thích
     */
    public void addToFavorites(String songId, OnResultListener<Void> listener) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            if (listener != null) listener.onError(new Exception("User not logged in"));
            return;
        }

        firestore.collection(USERS_COLLECTION).document(user.getUid())
                .update("favoriteSongs", FieldValue.arrayUnion(songId))
                .addOnSuccessListener(v -> {
                    // Cập nhật likeCount trong bài hát
                    firestore.collection(SONGS_COLLECTION).document(songId)
                            .update("likeCount", FieldValue.increment(1));
                    if (listener != null) listener.onSuccess(null);
                })
                .addOnFailureListener(e -> {
                    if (listener != null) listener.onError(e);
                });
    }

    /**
     * Xóa bài hát khỏi danh sách yêu thích
     */
    public void removeFromFavorites(String songId, OnResultListener<Void> listener) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            if (listener != null) listener.onError(new Exception("User not logged in"));
            return;
        }

        firestore.collection(USERS_COLLECTION).document(user.getUid())
                .update("favoriteSongs", FieldValue.arrayRemove(songId))
                .addOnSuccessListener(v -> {
                    // Cập nhật likeCount trong bài hát
                    firestore.collection(SONGS_COLLECTION).document(songId)
                            .update("likeCount", FieldValue.increment(-1));
                    if (listener != null) listener.onSuccess(null);
                })
                .addOnFailureListener(e -> {
                    if (listener != null) listener.onError(e);
                });
    }

    /**
     * Lấy danh sách bài hát theo IDs
     */
    public void getSongsByIds(List<String> songIds, OnResultListener<List<Song>> listener) {
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

