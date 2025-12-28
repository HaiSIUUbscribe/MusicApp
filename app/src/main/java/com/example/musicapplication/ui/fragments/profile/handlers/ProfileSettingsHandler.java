package com.example.musicapplication.ui.fragments.profile.handlers;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.musicapplication.R;
import com.example.musicapplication.data.repository.AuthRepository;
import com.example.musicapplication.data.repository.ProfileRepository;
import com.example.musicapplication.player.MusicPlayer;
import com.example.musicapplication.ui.activity.auth.LoginActivity;
import com.example.musicapplication.ui.activity.other.AboutActivity;
import com.example.musicapplication.ui.activity.other.PrivacyActivity;
import com.example.musicapplication.utils.ImageLoader;
import com.example.musicapplication.utils.ToastUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileSettingsHandler {
    private final Context context;
    private final AuthRepository authRepository;
    private final ProfileRepository profileRepository;

    // UI Elements
    private final TextView btnEditProfile;
    private final TextView btnPrivacy;
    private final TextView btnAbout;
    private final Button btnLogout;

    // Logic Variables
    private ImageView dialogAvatarPreview;
    private Uri selectedImageUri;
    private final ImagePickerCallback imagePickerCallback;
    private final Runnable onProfileUpdatedCallback;

    public interface ImagePickerCallback {
        void launchImagePicker();
    }

    public ProfileSettingsHandler(Context context, View view, AuthRepository authRepo, ProfileRepository profileRepo, ImagePickerCallback pickerCallback, Runnable updateCallback) {
        this.context = context;
        this.authRepository = authRepo;
        this.profileRepository = profileRepo;
        this.imagePickerCallback = pickerCallback;
        this.onProfileUpdatedCallback = updateCallback;

        this.btnEditProfile = view.findViewById(R.id.btn_edit_profile);
        this.btnPrivacy = view.findViewById(R.id.btn_privacy);
        this.btnAbout = view.findViewById(R.id.btn_about);
        this.btnLogout = view.findViewById(R.id.btn_logout);

        setupListeners();
    }

    private void setupListeners() {
        if (btnEditProfile != null) btnEditProfile.setOnClickListener(v -> showEditProfileDialog());

        if (btnPrivacy != null) btnPrivacy.setOnClickListener(v -> {
            context.startActivity(new Intent(context, PrivacyActivity.class));
        });

        if (btnAbout != null) btnAbout.setOnClickListener(v -> {
            context.startActivity(new Intent(context, AboutActivity.class));
        });

        if (btnLogout != null) btnLogout.setOnClickListener(v -> logout());
    }

    // Được gọi từ Fragment khi user đã chọn ảnh xong
    public void onImageSelected(Uri uri) {
        if (uri != null) {
            this.selectedImageUri = uri;
            if (dialogAvatarPreview != null) {
                dialogAvatarPreview.setImageURI(uri);
            }
        }
    }

    private void showEditProfileDialog() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            ToastUtils.showWarning(context, "Vui lòng đăng nhập");
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Chỉnh sửa hồ sơ");

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);

        dialogAvatarPreview = new ImageView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(200, 200);
        params.bottomMargin = 30;
        dialogAvatarPreview.setLayoutParams(params);
        dialogAvatarPreview.setScaleType(ImageView.ScaleType.CENTER_CROP);

        if (user.getPhotoUrl() != null) {
            ImageLoader.loadCircle(context, user.getPhotoUrl().toString(), dialogAvatarPreview);
        } else {
            dialogAvatarPreview.setImageResource(R.drawable.ic_profile);
        }

        // Gọi callback về Fragment để mở bộ chọn ảnh
        dialogAvatarPreview.setOnClickListener(v -> {
            if (imagePickerCallback != null) imagePickerCallback.launchImagePicker();
        });

        TextView tvHint = new TextView(context);
        tvHint.setText("Chạm để đổi ảnh");
        tvHint.setTextSize(12);
        tvHint.setGravity(Gravity.CENTER);
        tvHint.setPadding(0, 0, 0, 30);

        final EditText inputName = new EditText(context);
        inputName.setHint("Tên hiển thị");
        inputName.setText(user.getDisplayName());

        layout.addView(dialogAvatarPreview);
        layout.addView(tvHint);
        layout.addView(inputName);
        builder.setView(layout);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String newName = inputName.getText().toString().trim();
            if (newName.isEmpty()) {
                ToastUtils.showWarning(context, "Tên không được để trống");
                return;
            }
            performUpdateProfile(newName);
        });

        builder.setNegativeButton("Hủy", (d, w) -> {
            selectedImageUri = null;
            d.cancel();
        });

        builder.show();
    }

    private void performUpdateProfile(String newName) {
        ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Đang cập nhật...");
        pd.setCancelable(false);
        pd.show();

        if (selectedImageUri != null) {
            profileRepository.uploadProfileImage(selectedImageUri, new ProfileRepository.OnImageUploadListener() {
                @Override
                public void onSuccess(String imageUrl) {
                    updateProfileData(newName, imageUrl, pd);
                }
                @Override
                public void onError(Exception e) {
                    pd.dismiss();
                    ToastUtils.showError(context, "Lỗi upload ảnh: " + e.getMessage());
                }
            });
        } else {
            updateProfileData(newName, null, pd);
        }
    }

    private void updateProfileData(String newName, String imageUrl, ProgressDialog pd) {
        profileRepository.updateProfile(newName, imageUrl, new ProfileRepository.OnProfileUpdateListener() {
            @Override
            public void onSuccess() {
                pd.dismiss();
                ToastUtils.showSuccess(context, "Cập nhật thành công!");
                selectedImageUri = null;
                // Thông báo reload lại thông tin
                if (onProfileUpdatedCallback != null) onProfileUpdatedCallback.run();
            }
            @Override
            public void onError(Exception e) {
                pd.dismiss();
                ToastUtils.showError(context, "Lỗi: " + e.getMessage());
            }
        });
    }

    private void logout() {
        MusicPlayer.getInstance(context).stop();
        MusicPlayer.getInstance(context).release();
        authRepository.logout();
        ToastUtils.showInfo(context, "Đã đăng xuất");

        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}