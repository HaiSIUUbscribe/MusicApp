package com.example.musicapplication.ui.activity.upload.handlers;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import com.example.musicapplication.R;
import com.example.musicapplication.data.repository.AuthRepository;
import com.example.musicapplication.data.repository.SongUploadRepository;
import com.example.musicapplication.model.Song;
import com.example.musicapplication.utils.Logger;
import com.example.musicapplication.utils.ToastUtils;
import java.util.List;

public class UploadFirebaseHandler {
    private final Activity activity;
    private final SongUploadRepository songUploadRepository;
    private final AuthRepository authRepository;
    private final ProgressBar progressBar;
    private final Button btnUpload;

    public UploadFirebaseHandler(Activity activity) {
        this.activity = activity;
        this.songUploadRepository = new SongUploadRepository(activity);
        this.authRepository = new AuthRepository(activity);
        this.progressBar = activity.findViewById(R.id.progress_bar);
        this.btnUpload = activity.findViewById(R.id.btn_upload);
    }

    public boolean checkLogin() {
        if (!authRepository.isLoggedIn()) {
            ToastUtils.showWarning(activity, "Vui lòng đăng nhập để upload nhạc");
            activity.finish();
            return false;
        }
        return true;
    }

    public void startUpload(String title, String artist, String album, long duration,
                            List<String> tags, byte[] audioBytes, byte[] imageBytes) {

        AuthRepository.FirebaseUserWrapper user = authRepository.getCurrentUser();
        if (user == null) {
            ToastUtils.showWarning(activity, "Vui lòng đăng nhập");
            return;
        }

        Song song = new Song();
        song.title = title;
        song.artist = artist;
        song.album = album != null && !album.isEmpty() ? album : "";
        song.duration = duration;
        song.uploaderId = user.getUid();
        song.uploaderName = user.getEmail() != null ? user.getEmail().split("@")[0] : "User";
        song.uploadDate = System.currentTimeMillis();
        song.playCount = 0;
        song.likeCount = 0;
        song.tags = tags;

        setLoading(true);

        songUploadRepository.uploadSong(song, audioBytes, imageBytes, new SongUploadRepository.OnResultListener<String>() {
            @Override
            public void onSuccess(String songId) {
                Logger.d("Song uploaded successfully: " + songId);
                activity.runOnUiThread(() -> {
                    setLoading(false);
                    ToastUtils.showSuccess(activity, "Upload thành công!");
                    activity.finish();
                });
            }

            @Override
            public void onError(Exception error) {
                Logger.e("Lỗi upload bài hát", error);
                activity.runOnUiThread(() -> {
                    setLoading(false);
                    ToastUtils.showError(activity, "Lỗi upload: " + error.getMessage());
                });
            }
        });
    }

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnUpload.setEnabled(!isLoading);
    }
}