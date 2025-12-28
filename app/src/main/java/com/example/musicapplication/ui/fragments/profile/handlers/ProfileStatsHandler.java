package com.example.musicapplication.ui.fragments.profile.handlers;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;
import com.example.musicapplication.R;
import com.example.musicapplication.data.repository.PlaylistRepository;
import com.example.musicapplication.data.repository.UserRepository;
import com.example.musicapplication.model.Playlist;
import com.example.musicapplication.model.User;
import com.example.musicapplication.utils.Logger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class ProfileStatsHandler {
    private final Activity activity;
    private final UserRepository userRepository;
    private final PlaylistRepository playlistRepository;

    private final TextView tvFavoritesCount;
    private final TextView tvPlaylistsCount;

    public ProfileStatsHandler(Activity activity, View view, UserRepository userRepo, PlaylistRepository playlistRepo) {
        this.activity = activity;
        this.userRepository = userRepo;
        this.playlistRepository = playlistRepo;

        this.tvFavoritesCount = view.findViewById(R.id.tv_favorites_count);
        this.tvPlaylistsCount = view.findViewById(R.id.tv_playlists_count);
    }

    public void loadStats() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            resetStats();
            return;
        }

        // 1. Load Favorites Count (Từ User Firestore)
        userRepository.getUser(currentUser.getUid(), new UserRepository.OnResultListener<User>() {
            @Override
            public void onSuccess(User user) {
                if (activity != null) {
                    activity.runOnUiThread(() -> {
                        int count = (user != null && user.getFavoriteSongs() != null) ? user.getFavoriteSongs().size() : 0;
                        if (tvFavoritesCount != null) tvFavoritesCount.setText(String.valueOf(count));
                    });
                }
            }
            @Override public void onError(Exception e) { Logger.e("Error loading user stats", e); }
        });

        // 2. Load Playlists Count (Realtime)
        playlistRepository.getRealtimeUserPlaylists(new PlaylistRepository.OnResultListener<List<Playlist>>() {
            @Override
            public void onSuccess(List<Playlist> result) {
                if (activity != null) {
                    activity.runOnUiThread(() -> {
                        if (tvPlaylistsCount != null) tvPlaylistsCount.setText(String.valueOf(result.size()));
                    });
                }
            }
            @Override public void onError(Exception e) { Logger.e("Error loading playlist stats", e); }
        });
    }

    private void resetStats() {
        if (tvFavoritesCount != null) tvFavoritesCount.setText("0");
        if (tvPlaylistsCount != null) tvPlaylistsCount.setText("0");
    }
}