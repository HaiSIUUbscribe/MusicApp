package com.example.musicapplication.ui.activity.player.handlers;

import android.content.Context;
import android.media.AudioManager;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.example.musicapplication.R;

/**
 * Handler xử lý điều khiển âm lượng trong PlayerActivity
 */
public class PlayerVolumeHandler {
    
    private Context context;
    private AudioManager audioManager;
    private SeekBar seekBarVolume;
    private ImageView imgVolumeDown;
    private ImageView imgVolumeUp;
    
    private int maxVolume;
    private int currentVolume;

    public PlayerVolumeHandler(Context context) {
        this.context = context;
        this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        
        initViews();
        setupVolumeControls();
    }

    private void initViews() {
        seekBarVolume = ((android.app.Activity) context).findViewById(R.id.seekBarVolume);
        imgVolumeDown = ((android.app.Activity) context).findViewById(R.id.imgvolmnedown);
        imgVolumeUp = ((android.app.Activity) context).findViewById(R.id.imgvolmneup);
    }

    private void setupVolumeControls() {
        if (audioManager == null) return;
        
        // Lấy volume tối đa và hiện tại
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        
        // Set max và progress cho seekbar
        seekBarVolume.setMax(maxVolume);
        seekBarVolume.setProgress(currentVolume);
        
        // SeekBar change listener
        seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    setVolume(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Không cần xử lý
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Không cần xử lý
            }
        });
        
        // Volume Down button
        imgVolumeDown.setOnClickListener(v -> {
            int newVolume = Math.max(0, currentVolume - 1);
            setVolume(newVolume);
            seekBarVolume.setProgress(newVolume);
        });
        
        // Volume Up button
        imgVolumeUp.setOnClickListener(v -> {
            int newVolume = Math.min(maxVolume, currentVolume + 1);
            setVolume(newVolume);
            seekBarVolume.setProgress(newVolume);
        });
    }

    /**
     * Set volume level
     * @param volume Volume level (0 to maxVolume)
     */
    private void setVolume(int volume) {
        if (audioManager == null) return;
        
        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            volume,
            0  // Không hiện UI volume của hệ thống
        );
        currentVolume = volume;
    }

    /**
     * Cập nhật UI khi volume thay đổi từ bên ngoài (ví dụ: nút vật lý)
     */
    public void updateVolumeUI() {
        if (audioManager == null) return;
        
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        seekBarVolume.setProgress(currentVolume);
    }

    /**
     * Lấy volume hiện tại (0-100%)
     */
    public int getCurrentVolumePercentage() {
        if (maxVolume == 0) return 0;
        return (currentVolume * 100) / maxVolume;
    }
}
