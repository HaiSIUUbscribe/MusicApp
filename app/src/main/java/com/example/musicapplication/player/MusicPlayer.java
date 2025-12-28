package com.example.musicapplication.player;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import com.example.musicapplication.model.Song;
import com.example.musicapplication.utils.Logger;
import com.example.musicapplication.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MusicPlayer {
    private static MusicPlayer instance;
    private final MediaPlayer mediaPlayer;
    private String currentUri;
    private final Context ctx;
    private final AudioManager audioManager;

    // --- DANH SÁCH LISTENER (Quan trọng cho MiniPlayer) ---
    // Dùng List để nhiều màn hình cùng lắng nghe được sự kiện
    private final List<OnCompletionListener> listeners = new ArrayList<>();

    private List<Song> playlist;
    private int currentSongIndex = -1;

    // --- CÁC BIẾN TRẠNG THÁI ---
    private boolean isRepeatEnabled = false;
    private boolean isShuffleEnabled = false;
    private boolean isPreparing = false;
    private boolean isPrepared = false;

    public interface OnCompletionListener {
        void onCompletion();
        void onNextSong(Song song);
        void onPreviousSong(Song song);
    }

    private MusicPlayer(Context context) {
        ctx = context.getApplicationContext();
        mediaPlayer = new MediaPlayer();
        audioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            mediaPlayer.setAudioAttributes(audioAttributes);
        } else {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
    }

    public static synchronized MusicPlayer getInstance(Context context) {
        if (instance == null) instance = new MusicPlayer(context);
        return instance;
    }

    // --- CÁC HÀM CƠ BẢN (GIỮ NGUYÊN) ---

    public void play(String uri) {
        try {
            if (uri == null || uri.isEmpty()) {
                Logger.e("URI is null/empty");
                return;
            }

            if (isPreparing) return; // Đang load dở thì thôi

            // Nếu phát lại đúng bài đang hát thì bỏ qua (trừ khi đang pause thì không gọi play mà gọi resume)
            if (uri.equals(currentUri) && mediaPlayer.isPlaying()) {
                return;
            }

            mediaPlayer.reset();
            isPreparing = true;
            isPrepared = false;
            currentUri = uri;

            Uri audioUri = Uri.parse(uri);
            mediaPlayer.setDataSource(ctx, audioUri);

            // Xử lý Volume
            int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (currentVolume == 0) {
                int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume / 3, 0);
            }

            // Cập nhật trạng thái lặp lại
            mediaPlayer.setLooping(isRepeatEnabled);

            mediaPlayer.setOnPreparedListener(mp -> {
                isPreparing = false;
                isPrepared = true;
                mp.start();
                ToastUtils.showInfo(ctx, "Đang phát: " + getSongTitle(uri));
            });

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                isPreparing = false;
                Logger.e( "MediaPlayer Error: " + what);
                return true;
            });

            // Xử lý khi hết bài
            mediaPlayer.setOnCompletionListener(mp -> {
                // Nếu đang Loop (Repeat One) thì MediaPlayer tự phát lại, không cần báo Next
                if (!mediaPlayer.isLooping()) {
                    notifyCompletion(); // Báo cho tất cả màn hình biết
                }
            });

            mediaPlayer.prepareAsync();

        } catch (Exception e) {
            isPreparing = false;
            Logger.e("Exception: " + e.getMessage());
            ToastUtils.showError(ctx, "Lỗi phát " + getSongTitle(uri) );
        }
    }

    public void pause() { if (mediaPlayer.isPlaying()) mediaPlayer.pause(); }
    public void resume() { if (!mediaPlayer.isPlaying()) mediaPlayer.start(); }

    public void stop() {
        if (mediaPlayer.isPlaying()) mediaPlayer.stop();
        mediaPlayer.reset();
        currentUri = null;
        isPreparing = false;
        isPrepared = false;
    }
    public void release() {
        mediaPlayer.release();
        instance = null;
    }

    public boolean isPlaying() { return mediaPlayer.isPlaying(); }
    public String getCurrentUri() { return currentUri; }

    public int getCurrentPosition() {
        return (mediaPlayer != null && isPrepared) ? mediaPlayer.getCurrentPosition() : 0;
    }

    public int getDuration() {
        return (mediaPlayer != null && isPrepared) ? mediaPlayer.getDuration() : 0;
    }

    public void seekTo(int position) { mediaPlayer.seekTo(position); }

    // --- QUẢN LÝ LISTENER (NÂNG CẤP CHO MINIPLAYER) ---

    public void addListener(OnCompletionListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(OnCompletionListener listener) {
        listeners.remove(listener);
    }

    // Hàm cũ (giữ lại để tương thích ngược, nhưng chuyển sang dùng list)
    public void setOnCompletionListener(OnCompletionListener listener) {
        listeners.clear();
        listeners.add(listener);
    }

    // Các hàm helper để thông báo cho TOÀN BỘ listener
    private void notifyCompletion() {
        for (OnCompletionListener listener : listeners) {
            listener.onCompletion();
        }
    }

    private void notifyNextSong(Song song) {
        for (OnCompletionListener listener : listeners) {
            listener.onNextSong(song);
        }
    }

    private void notifyPreviousSong(Song song) {
        for (OnCompletionListener listener : listeners) {
            listener.onPreviousSong(song);
        }
    }

    // --- QUẢN LÝ PLAYLIST & LOGIC SHUFFLE/REPEAT ---

    public void setPlaylist(List<Song> playlist) { this.playlist = playlist; }

    public void setPlaylist(List<Song> playlist, int currentIndex) {
        this.playlist = playlist;
        this.currentSongIndex = currentIndex;
    }

    public void setShuffleEnabled(boolean enabled) { this.isShuffleEnabled = enabled; }
    public boolean isShuffleEnabled() { return isShuffleEnabled; }

    public void setRepeatEnabled(boolean enabled) {
        this.isRepeatEnabled = enabled;
        if (mediaPlayer != null) {
            mediaPlayer.setLooping(enabled);
        }
    }
    public boolean isRepeatEnabled() { return isRepeatEnabled; }

    public void playNext() {
        if (playlist == null || playlist.isEmpty()) return;

        if (isShuffleEnabled && playlist.size() > 1) {
            // Random bài khác bài hiện tại
            int newIndex;
            Random random = new Random();
            do {
                newIndex = random.nextInt(playlist.size());
            } while (newIndex == currentSongIndex);
            currentSongIndex = newIndex;
        } else {
            // Tăng dần
            currentSongIndex++;
            if (currentSongIndex >= playlist.size()) currentSongIndex = 0;
        }

        playSongAtIndex(currentSongIndex, true);
    }

    public void playPrevious() {
        if (playlist == null || playlist.isEmpty()) return;

        currentSongIndex--;
        if (currentSongIndex < 0) currentSongIndex = playlist.size() - 1;

        playSongAtIndex(currentSongIndex, false);
    }

    private void playSongAtIndex(int index, boolean isNext) {
        if (index < 0 || index >= playlist.size()) return;

        Song song = playlist.get(index);
        play(song.getAudioUrl());

        if (isNext) notifyNextSong(song);
        else notifyPreviousSong(song);
    }

    public Song getCurrentSong() {
        if (playlist != null && currentSongIndex >= 0 && currentSongIndex < playlist.size()) {
            return playlist.get(currentSongIndex);
        }
        return null;
    }

    public int getCurrentSongIndex() { return currentSongIndex; }
    public void setCurrentSongIndex(int index) { this.currentSongIndex = index; }

    private String getSongTitle(String uri) {
        if (playlist != null && currentSongIndex >= 0 && currentSongIndex < playlist.size()) {
            return playlist.get(currentSongIndex).getTitle();
        }
        return "bài hát";
    }
}