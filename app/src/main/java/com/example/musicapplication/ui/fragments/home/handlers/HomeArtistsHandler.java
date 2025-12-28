package com.example.musicapplication.ui.fragments.home.handlers;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapplication.R;
import com.example.musicapplication.data.repository.ArtistRepository;
import com.example.musicapplication.model.Artist;
import com.example.musicapplication.ui.adapter.ArtistAdapter;
import com.example.musicapplication.utils.Logger;
import com.example.musicapplication.utils.ToastUtils;

public class HomeArtistsHandler {
    private Context context;
    private RecyclerView recyclerArtists;
    private ArtistAdapter artistAdapter;
    private ArtistRepository artistRepository;
    private OnArtistClickListener listener;

    public interface OnArtistClickListener {
        void onArtistClick(Artist artist);
    }

    public HomeArtistsHandler(Context context, View view, ArtistRepository artistRepository, OnArtistClickListener listener) {
        this.context = context;
        this.artistRepository = artistRepository;
        this.listener = listener;

        recyclerArtists = view.findViewById(R.id.recycler_artists);
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        // Horizontal LinearLayoutManager for artist cards
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        recyclerArtists.setLayoutManager(layoutManager);

        // Initialize adapter
        artistAdapter = new ArtistAdapter(context, artist -> {
            if (listener != null) {
                listener.onArtistClick(artist);
            }
        });
        recyclerArtists.setAdapter(artistAdapter);
    }

    public void loadData() {
        loadData(null);
    }

    public void loadData(Runnable onComplete) {
        // Load top 10 popular artists
        artistRepository.getPopularArtists(10, new ArtistRepository.OnArtistsLoadedListener() {
            @Override
            public void onSuccess(java.util.List<Artist> artists) {
                Logger.d("Loaded " + artists.size() + " artists for home");
                artistAdapter.setArtists(artists);
                if (onComplete != null) onComplete.run();
            }

            @Override
            public void onError(Exception error) {
                Logger.e("Error loading artists for home", error);
                // Hiển thị thông báo cụ thể cho lỗi mạng
                String message = error.getMessage();
                if (message != null && message.contains("mạng")) {
                    ToastUtils.showError(context, message);
                } else {
                    ToastUtils.showError(context, "Không thể tải danh sách nghệ sĩ");
                }
                if (onComplete != null) onComplete.run();
            }
        });
    }
}
