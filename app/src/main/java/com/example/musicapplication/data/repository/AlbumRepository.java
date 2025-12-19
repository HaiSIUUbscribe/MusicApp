package com.example.musicapplication.data.repository;

import android.content.Context;
import android.util.Log;

import com.example.musicapplication.model.Album;
import com.example.musicapplication.model.Song;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository chuyên xử lý các thao tác liên quan đến Album
 */
public class AlbumRepository {
    private static final String TAG = "AlbumRepository";
    private static final String ALBUMS_COLLECTION = "albums";
    private static final String SONGS_COLLECTION = "songs";

    private FirebaseFirestore firestore;
    private Context context;

    public interface OnResultListener<T> {
        void onSuccess(T result);
        void onError(Exception error);
    }

    public AlbumRepository(Context context) {
        this.context = context.getApplicationContext();
        this.firestore = FirebaseFirestore.getInstance();
    }

    /**
     * Lấy tất cả Albums từ Firestore
     */
    public void getAlbums(OnResultListener<List<Album>> listener) {
        firestore.collection(ALBUMS_COLLECTION).get()
                .addOnSuccessListener(snapshots -> {
                    List<Album> albums = new ArrayList<>();
                    for (var doc : snapshots) {
                        try {
                            String title = doc.getString("title");
                            if (title == null) title = doc.getString("name");
                            String coverUrl = doc.getString("coverUrl");
                            if (coverUrl == null) coverUrl = doc.getString("imageUrl");
                            if (title != null) {
                                albums.add(new Album(doc.getId(), title, doc.getString("artist"), coverUrl));
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing album", e);
                        }
                    }
                    listener.onSuccess(albums);
                })
                .addOnFailureListener(listener::onError);
    }

    /**
     * Lấy danh sách bài hát theo tên Album
     */
    public void getSongsByAlbum(String albumName, OnResultListener<List<Song>> listener) {
        firestore.collection(SONGS_COLLECTION)
                .whereEqualTo("album", albumName)
                .get()
                .addOnSuccessListener(snapshots -> {
                    List<Song> songs = new ArrayList<>();
                    for (var doc : snapshots) {
                        Song song = documentToSong(doc.getId(), doc.getData());
                        if (song != null) songs.add(song);
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