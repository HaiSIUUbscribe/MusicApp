package com.example.musicapplication.ui.activity.upload;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapplication.R;
import com.example.musicapplication.ui.activity.upload.handlers.UploadFilePickerHandler;
import com.example.musicapplication.ui.activity.upload.handlers.UploadFirebaseHandler;
import com.example.musicapplication.ui.activity.upload.handlers.UploadMetadataHandler;
import com.example.musicapplication.ui.activity.upload.handlers.UploadValidationHandler;
import com.example.musicapplication.utils.PermissionUtils;
import com.example.musicapplication.utils.ToastUtils;

public class UploadSongActivity extends AppCompatActivity {

    private Button btnSelectAudio, btnSelectImage, btnUpload;

    // Handlers
    private UploadFilePickerHandler filePickerHandler;
    private UploadMetadataHandler metadataHandler;
    private UploadValidationHandler validationHandler;
    private UploadFirebaseHandler firebaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_song);

        initViews();
        initHandlers();

        // Kiểm tra đăng nhập ngay khi vào
        if (!firebaseHandler.checkLogin()) return;

        setupClickListeners();
    }

    private void initViews() {
        btnSelectAudio = findViewById(R.id.btn_select_audio);
        btnSelectImage = findViewById(R.id.btn_select_image);
        btnUpload = findViewById(R.id.btn_upload);
    }

    private void initHandlers() {
        metadataHandler = new UploadMetadataHandler(this);
        validationHandler = new UploadValidationHandler(this);
        firebaseHandler = new UploadFirebaseHandler(this);

        // FilePickerHandler cần callback để cập nhật UI/Metadata khi chọn xong
        filePickerHandler = new UploadFilePickerHandler(this, new UploadFilePickerHandler.OnFileSelectedListener() {
            @Override
            public void onAudioSelected(Uri uri) {
                btnSelectAudio.setText("✓ Đã chọn file nhạc");
                // Tự động trích xuất metadata khi chọn nhạc xong
                byte[] embeddedImage = metadataHandler.extractAudioMetadata(uri);
                if (embeddedImage != null) {
                    filePickerHandler.setImageBytes(embeddedImage);
                }
            }

            @Override
            public void onImageSelected(Uri uri) {
                btnSelectImage.setText("✓ Đã chọn ảnh");
                metadataHandler.displayImage(uri);
            }
        });
    }

    private void setupClickListeners() {
        btnSelectAudio.setOnClickListener(v -> {
            if (PermissionUtils.hasStoragePermission(this)) {
                filePickerHandler.selectAudio();
            } else {
                PermissionUtils.requestStoragePermission(this);
            }
        });

        btnSelectImage.setOnClickListener(v -> {
            if (PermissionUtils.hasStoragePermission(this)) {
                filePickerHandler.selectImage();
            } else {
                PermissionUtils.requestStoragePermission(this);
            }
        });

        btnUpload.setOnClickListener(v -> {
            byte[] audioBytes = filePickerHandler.getAudioBytes();

            // Bước 1: Validate
            if (validationHandler.validate(audioBytes)) {
                // Bước 2: Gọi Firebase Handler để upload
                firebaseHandler.startUpload(
                        validationHandler.getTitle(),
                        validationHandler.getArtist(),
                        validationHandler.getAlbum(),
                        metadataHandler.getCurrentDuration(),
                        validationHandler.getTags(),audioBytes,
                        filePickerHandler.getImageBytes()
                );
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Chuyển kết quả sang Handler xử lý
        filePickerHandler.handleActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionUtils.REQUEST_CODE_STORAGE) {
            if (PermissionUtils.isPermissionGranted(grantResults)) {
                ToastUtils.showSuccess(this, "Đã cấp quyền");
            } else {
                ToastUtils.showWarning(this, "Cần quyền để chọn file");
            }
        }
    }
}