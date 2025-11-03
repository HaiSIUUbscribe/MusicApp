package com.example.musicapplication.player;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

public class MusicPlayer {
    private static final String TAG = "MusicPlayer";
    private static MusicPlayer instance;
    private final MediaPlayer mediaPlayer;
    private String currentUri;
    private final Context ctx;
    private AudioManager audioManager;

    private MusicPlayer(Context context) {
        ctx = context.getApplicationContext();
        mediaPlayer = new MediaPlayer();
        audioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);

        // Set audio attributes for music playback
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            mediaPlayer.setAudioAttributes(audioAttributes);
        } else {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }

        Log.d(TAG, "MusicPlayer initialized with audio attributes");
    }

    public static synchronized MusicPlayer getInstance(Context context) {
        if (instance == null) instance = new MusicPlayer(context);
        return instance;
    }

    public void play(String uri) {
        try {
            if (uri == null) {
                Log.e(TAG, "URI is null, cannot play");
                Toast.makeText(ctx, "Không thể phát: URI null", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra volume
            int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            Log.d(TAG, "Current volume: " + currentVolume + "/" + maxVolume);

            if (currentVolume == 0) {
                Toast.makeText(ctx, "⚠️ Âm lượng đang ở mức 0. Vui lòng tăng âm lượng!", Toast.LENGTH_LONG).show();
                // Tự động set volume về 50%
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume / 2, AudioManager.FLAG_SHOW_UI);
            }

            Log.d(TAG, "Attempting to play: " + uri);

            if (uri.equals(currentUri) && mediaPlayer.isPlaying()) {
                Log.d(TAG, "Already playing this song");
                return;
            }

            mediaPlayer.reset();

            // Set lại audio attributes sau reset
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build();
                mediaPlayer.setAudioAttributes(audioAttributes);
            } else {
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            }

            currentUri = uri;

            Uri audioUri = Uri.parse(uri);
            Log.d(TAG, "Setting data source: " + audioUri);
            mediaPlayer.setDataSource(ctx, audioUri);

            // Dùng prepareAsync để không block UI thread
            mediaPlayer.setOnPreparedListener(mp -> {
                Log.d(TAG, "MediaPlayer prepared, starting playback");
                Log.d(TAG, "MediaPlayer duration: " + mp.getDuration() + "ms");
                mp.setVolume(1.0f, 1.0f); // Set volume to max
                mp.start();
                Log.d(TAG, "MediaPlayer started. isPlaying: " + mp.isPlaying());
                Toast.makeText(ctx, "🎵 Đang phát nhạc...", Toast.LENGTH_SHORT).show();
            });

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                String errorMsg = "Unknown error";
                if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
                    errorMsg = "Media server died";
                } else if (what == MediaPlayer.MEDIA_ERROR_UNKNOWN) {
                    errorMsg = "Unknown media error";
                }
                Log.e(TAG, "MediaPlayer error - what: " + what + " (" + errorMsg + "), extra: " + extra);
                Toast.makeText(ctx, "❌ Lỗi phát nhạc: " + errorMsg, Toast.LENGTH_LONG).show();
                return true;
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                Log.d(TAG, "Playback completed");
                Toast.makeText(ctx, "✅ Phát xong bài hát", Toast.LENGTH_SHORT).show();
            });

            mediaPlayer.prepareAsync();
            Log.d(TAG, "PrepareAsync called");

        } catch (IOException e) {
            Log.e(TAG, "IOException while playing music: " + e.getMessage(), e);
            Toast.makeText(ctx, "❌ Lỗi I/O: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (IllegalStateException e) {
            Log.e(TAG, "IllegalStateException: " + e.getMessage(), e);
            Toast.makeText(ctx, "❌ Trạng thái không hợp lệ: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error: " + e.getMessage(), e);
            Toast.makeText(ctx, "❌ Lỗi không xác định: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void pause() {
        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                Log.d(TAG, "Paused");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error pausing", e);
        }
    }

    public void resume() {
        try {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                Log.d(TAG, "Resumed");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error resuming", e);
        }
    }

    public void stop() {
        try {
            if (mediaPlayer.isPlaying()) mediaPlayer.stop();
            mediaPlayer.reset();
            currentUri = null;
            Log.d(TAG, "Stopped");
        } catch (Exception e) {
            Log.e(TAG, "Error stopping", e);
        }
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public String getCurrentUri() {
        return currentUri;
    }
}
