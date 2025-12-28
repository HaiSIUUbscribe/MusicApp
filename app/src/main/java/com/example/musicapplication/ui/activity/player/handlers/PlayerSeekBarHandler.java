package com.example.musicapplication.ui.activity.player.handlers;

import android.os.Handler;
import android.os.Looper;
import android.widget.SeekBar;
import android.widget.TextView;
import com.example.musicapplication.R;
import com.example.musicapplication.player.MusicPlayer;
import com.example.musicapplication.ui.activity.player.PlayerActivity;
import com.example.musicapplication.utils.TimeFormatter;

public class PlayerSeekBarHandler {
    private final PlayerActivity activity;
    private final MusicPlayer player;
    private SeekBar seekBar;
    private TextView currentTime, totalTime;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean isTrackingTouch = false;
    private boolean isRunning = false; // Biến kiểm soát vòng lặp thủ công

    public PlayerSeekBarHandler(PlayerActivity activity, MusicPlayer player) {
        this.activity = activity;
        this.player = player;
        initViews();
        setupListener();
    }

    private void initViews() {
        seekBar = activity.findViewById(R.id.seek_bar);
        currentTime = activity.findViewById(R.id.txt_current_time);
        totalTime = activity.findViewById(R.id.txt_total_time);
    }

    private void setupListener() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    // Cập nhật text thời gian ngay khi kéo để user thấy mượt
                    currentTime.setText(TimeFormatter.formatDuration(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isTrackingTouch = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isTrackingTouch = false;
                // Khi thả tay ra mới seek nhạc
                player.seekTo(seekBar.getProgress());
            }
        });
    }

    private final Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isRunning) return; // Nếu đã stop thì dừng hẳn

            // Chỉ cập nhật UI nếu user không đang giữ thanh seekbar
            if (!isTrackingTouch && player.isPlaying()) {
                try {
                    int current = player.getCurrentPosition();
                    int duration = player.getDuration();

                    if (duration > 0) {
                        seekBar.setMax(duration);
                        seekBar.setProgress(current);
                        currentTime.setText(TimeFormatter.formatDuration(current));
                        totalTime.setText(TimeFormatter.formatDuration(duration));
                    }
                } catch (Exception ignored) {}
            }

            // QUAN TRỌNG: Luôn chạy lại sau 1 giây miễn là isRunning = true
            // Bất kể player có đang play hay không, vòng lặp vẫn sống để chờ trạng thái
            handler.postDelayed(this, 1000);
        }
    };

    public void startUpdating() {
        if (isRunning) return; // Tránh chạy trùng lặp
        isRunning = true;
        handler.removeCallbacks(updateRunnable);
        handler.post(updateRunnable);
    }

    public void stopUpdating() {
        isRunning = false;
        handler.removeCallbacks(updateRunnable);
    }

    public void reset() {
        currentTime.setText("0:00");
        seekBar.setProgress(0);
    }
}