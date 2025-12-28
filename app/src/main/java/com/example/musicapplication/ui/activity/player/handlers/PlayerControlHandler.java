package com.example.musicapplication.ui.activity.player.handlers;

import android.view.View;
import android.widget.ImageButton;
import androidx.core.content.ContextCompat;
import com.example.musicapplication.R;
import com.example.musicapplication.player.MusicPlayer;
import com.example.musicapplication.ui.activity.player.PlayerActivity;
import com.example.musicapplication.utils.ToastUtils;

public class PlayerControlHandler {
    private final PlayerActivity activity;
    private final MusicPlayer player;
    private ImageButton btnPlayPause, btnShuffle, btnRepeat, btnNext, btnPrevious;

    public PlayerControlHandler(PlayerActivity activity, MusicPlayer player) {
        this.activity = activity;
        this.player = player;
        initViews();
        setupListeners();
    }

    private void initViews() {
        btnPlayPause = activity.findViewById(R.id.btn_play);
        btnShuffle = activity.findViewById(R.id.btn_shuffle);
        btnRepeat = activity.findViewById(R.id.btn_repeat);
        btnNext = activity.findViewById(R.id.btn_next);
        btnPrevious = activity.findViewById(R.id.btn_previous);
    }

    private void setupListeners() {
        btnPlayPause.setOnClickListener(v -> {
            if (player.isPlaying()) {
                player.pause();
                activity.updatePlayPauseUI(false);
            } else {
                player.resume();
                activity.updatePlayPauseUI(true);
            }
        });

        btnNext.setOnClickListener(v -> player.playNext());
        btnPrevious.setOnClickListener(v -> player.playPrevious());

        btnShuffle.setOnClickListener(v -> {
            boolean newState = !player.isShuffleEnabled();
            player.setShuffleEnabled(newState);
            updateShuffleRepeatUI();
            ToastUtils.showInfo(activity, newState ? "Trộn bài: BẬT" : "Trộn bài: TẮT");
        });

        btnRepeat.setOnClickListener(v -> {
            boolean newState = !player.isRepeatEnabled();
            player.setRepeatEnabled(newState);
            updateShuffleRepeatUI();
            ToastUtils.showInfo(activity, newState ? "Lặp lại: BẬT" : "Lặp lại: TẮT");
        });
    }

    public void updatePlayPauseUI(boolean isPlaying) {
        btnPlayPause.setImageResource(isPlaying ? android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play);
    }

    public void updateShuffleRepeatUI() {
        int shuffleColor = player.isShuffleEnabled() ? R.color.accent_orange : R.color.text_secondary;
        btnShuffle.setColorFilter(ContextCompat.getColor(activity, shuffleColor));

        int repeatColor = player.isRepeatEnabled() ? R.color.accent_orange : R.color.text_secondary;
        btnRepeat.setColorFilter(ContextCompat.getColor(activity, repeatColor));
    }
}