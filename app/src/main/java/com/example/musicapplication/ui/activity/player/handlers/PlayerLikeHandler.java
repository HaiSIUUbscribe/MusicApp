package com.example.musicapplication.ui.activity.player.handlers;

import android.widget.ImageButton;
import androidx.core.content.ContextCompat;
import com.example.musicapplication.R;
import com.example.musicapplication.data.repository.FavoriteRepository;
import com.example.musicapplication.ui.activity.player.PlayerActivity;
import com.example.musicapplication.utils.ToastUtils;

public class PlayerLikeHandler {
    private final PlayerActivity activity;
    private final FavoriteRepository favoriteRepository;
    private ImageButton btnLike;
    private boolean isLiked = false;

    public PlayerLikeHandler(PlayerActivity activity, FavoriteRepository repository) {
        this.activity = activity;
        this.favoriteRepository = repository;
        this.btnLike = activity.findViewById(R.id.btn_like);
        setupListener();
    }

    private void setupListener() {
        btnLike.setOnClickListener(v -> toggleLike());
    }

    public void checkLikeStatus(String songId) {
        if (songId == null) return;
        favoriteRepository.checkIsLiked(songId, new FavoriteRepository.OnResultListener<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                isLiked = result;
                updateUI();
            }
            @Override
            public void onError(Exception error) {
                ToastUtils.showError(activity, "Lỗi kết nối!");
            }
        });
    }

    private void toggleLike() {
        String currentSongId = activity.getCurrentSongId();
        if (currentSongId == null) return;

        isLiked = !isLiked;
        updateUI();

        favoriteRepository.updateFavorite(currentSongId, isLiked, new FavoriteRepository.OnResultListener<Boolean>() {
            @Override public void onSuccess(Boolean result) {}
            @Override
            public void onError(Exception error) {
                isLiked = !isLiked; // Revert UI on error
                updateUI();
                ToastUtils.showError(activity, "Lỗi kết nối!");
            }
        });
    }

    private void updateUI() {
        if (isLiked) {
            btnLike.setImageResource(R.drawable.ic_heart_filled);
            btnLike.setColorFilter(ContextCompat.getColor(activity, R.color.accent_secondary));
        } else {
            btnLike.setImageResource(R.drawable.ic_heart);
            btnLike.setColorFilter(ContextCompat.getColor(activity, R.color.white));
        }
    }
}