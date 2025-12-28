package com.example.musicapplication.ui.activity.upload.handlers;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import com.example.musicapplication.utils.Logger;
import com.example.musicapplication.utils.ToastUtils;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class UploadFilePickerHandler {
    public static final int PICK_AUDIO = 100;
    public static final int PICK_IMAGE = 101;

    private final Activity activity;
    private Uri audioUri;
    private Uri imageUri;
    private byte[] audioBytes;
    private byte[] imageBytes;

    // Callback để thông báo khi file đã được chọn và xử lý xong
    private final OnFileSelectedListener listener;

    public interface OnFileSelectedListener {
        void onAudioSelected(Uri uri);
        void onImageSelected(Uri uri);
    }

    public UploadFilePickerHandler(Activity activity, OnFileSelectedListener listener) {
        this.activity = activity;
        this.listener = listener;
    }

    public void selectAudio() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        activity.startActivityForResult(Intent.createChooser(intent, "Chọn file nhạc"), PICK_AUDIO);
    }

    public void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        activity.startActivityForResult(Intent.createChooser(intent, "Chọn ảnh bìa"), PICK_IMAGE);
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == PICK_AUDIO) {
                processAudio(data.getData());
            } else if (requestCode == PICK_IMAGE) {
                processImage(data.getData());
            }
        }
    }

    private void processAudio(Uri uri) {
        this.audioUri = uri;
        if (audioUri != null) {
            try {
                InputStream inputStream = activity.getContentResolver().openInputStream(audioUri);
                this.audioBytes = readInputStream(inputStream);
                ToastUtils.showSuccess(activity, "Đã chọn file nhạc");
                if (listener != null) listener.onAudioSelected(uri);
            } catch (Exception e) {
                Logger.e("Lỗi đọc file nhạc", e);
                ToastUtils.showError(activity, "Lỗi đọc file nhạc");
            }
        }
    }

    private void processImage(Uri uri) {
        this.imageUri = uri;
        if (imageUri != null) {
            try {
                InputStream inputStream = activity.getContentResolver().openInputStream(imageUri);
                this.imageBytes = readInputStream(inputStream);
                ToastUtils.showSuccess(activity, "Đã chọn ảnh bìa");
                if (listener != null) listener.onImageSelected(uri);
            } catch (Exception e) {
                Logger.e("Lỗi đọc file ảnh", e);
                ToastUtils.showError(activity, "Lỗi đọc file ảnh");
            }
        }
    }

    private byte[] readInputStream(InputStream inputStream) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[8192];
        int nRead;
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }

    // Getters
    public byte[] getAudioBytes() { return audioBytes; }
    public byte[] getImageBytes() { return imageBytes; }
    public void setImageBytes(byte[] bytes) { this.imageBytes = bytes; } // Dùng khi extract từ metadata
}