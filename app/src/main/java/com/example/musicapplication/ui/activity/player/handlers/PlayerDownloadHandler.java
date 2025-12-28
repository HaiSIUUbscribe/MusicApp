package com.example.musicapplication.ui.activity.player.handlers;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import com.example.musicapplication.ui.activity.player.PlayerActivity;
import com.example.musicapplication.utils.ToastUtils;

public class PlayerDownloadHandler {
    private final PlayerActivity activity;

    public PlayerDownloadHandler(PlayerActivity activity) {
        this.activity = activity;
    }

    public void downloadSong(String audioUrl, String songTitle) {
        if (audioUrl == null || !audioUrl.startsWith("http")) {
            ToastUtils.showWarning(activity, "Không thể tải bài hát Offline");
            return;
        }
        try {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(audioUrl));
            request.setTitle(songTitle);
            request.setDescription("Đang tải nhạc...");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC, songTitle + ".mp3");

            DownloadManager manager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);
            ToastUtils.showInfo(activity, "Đang bắt đầu tải xuống...");
        } catch (Exception e) {
            ToastUtils.showError(activity, "Lỗi tải xuống: " + e.getMessage());
        }
    }
}