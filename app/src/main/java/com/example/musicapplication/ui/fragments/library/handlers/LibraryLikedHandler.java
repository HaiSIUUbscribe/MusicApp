package com.example.musicapplication.ui.fragments.library.handlers;

import android.content.Context;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.musicapplication.R;
import com.example.musicapplication.data.repository.FavoriteRepository;
import com.example.musicapplication.model.Song;
import com.example.musicapplication.ui.adapter.SongListAdapter;
import com.example.musicapplication.utils.Logger;
import com.google.firebase.firestore.ListenerRegistration;
import java.util.ArrayList;
import java.util.List;

public class LibraryLikedHandler {
    private final Context context;
    private final FavoriteRepository favoriteRepository;
    private final RecyclerView recyclerLiked;

    private SongListAdapter likedSongAdapter;
    private final List<Song> allLikedSongs = new ArrayList<>();
    private final List<Song> displayLikedSongs = new ArrayList<>();
    private ListenerRegistration likedSongsListener;

    public LibraryLikedHandler(Context context, View view, FavoriteRepository repository, SongListAdapter.OnSongClickListener listener) {
        this.context = context;
        this.favoriteRepository = repository;
        this.recyclerLiked = view.findViewById(R.id.recycler_liked_songs);

        setupRecyclerView(listener);
    }

    private void setupRecyclerView(SongListAdapter.OnSongClickListener listener) {
        recyclerLiked.setLayoutManager(new LinearLayoutManager(context));
        likedSongAdapter = new SongListAdapter(context, displayLikedSongs, listener);
        recyclerLiked.setAdapter(likedSongAdapter);
    }

    public void startListening() {
        startListening(null);
    }

    public void startListening(Runnable onComplete) {
        likedSongsListener = favoriteRepository.listenToLikedSongs(new FavoriteRepository.OnResultListener<List<Song>>() {
            @Override
            public void onSuccess(List<Song> result) {
                allLikedSongs.clear();
                allLikedSongs.addAll(result);
                filter("");
                if (onComplete != null) onComplete.run();
            }
            @Override
            public void onError(Exception e) {
                Logger.e("Error loading liked songs", e);
                if (onComplete != null) onComplete.run();
            }
        });
    }

    public void filter(String query) {
        displayLikedSongs.clear();
        String lowerQuery = query.toLowerCase().trim();
        if (lowerQuery.isEmpty()) {
            displayLikedSongs.addAll(allLikedSongs);
        } else {
            for (Song s : allLikedSongs) {
                if (s.getTitle().toLowerCase().contains(lowerQuery) || s.getArtist().toLowerCase().contains(lowerQuery)) {
                    displayLikedSongs.add(s);
                }
            }
        }
        likedSongAdapter.notifyDataSetChanged();
    }

    public void stopListening() {
        if (likedSongsListener != null) likedSongsListener.remove();
    }
}