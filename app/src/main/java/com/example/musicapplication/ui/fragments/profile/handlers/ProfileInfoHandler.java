package com.example.musicapplication.ui.fragments.profile.handlers;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.musicapplication.R;
import com.example.musicapplication.data.repository.UserRepository;
import com.example.musicapplication.model.User;
import com.example.musicapplication.utils.ImageLoader;
import com.example.musicapplication.utils.Logger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileInfoHandler {
    private final Activity activity;
    private final UserRepository userRepository;

    private final ImageView imgAvatar;
    private final TextView tvDisplayName;
    private final TextView tvEmail;

    public ProfileInfoHandler(Activity activity, View view, UserRepository userRepo) {
        this.activity = activity;
        this.userRepository = userRepo;

        this.imgAvatar = view.findViewById(R.id.img_avatar);
        this.tvDisplayName = view.findViewById(R.id.tv_display_name);
        this.tvEmail = view.findViewById(R.id.tv_email);
    }

    public void loadUserInfo() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            displayDefaultInfo();
            return;
        }

        // Hiển thị thông tin cơ bản từ Auth trước
        displayBasicUserInfo(currentUser);

        // Tải thông tin chi tiết từ Firestore (để lấy ảnh mới nhất nếu Auth chưa sync)
        userRepository.getUser(currentUser.getUid(), new UserRepository.OnResultListener<User>() {
            @Override
            public void onSuccess(User user) {
                if (activity != null) {
                    activity.runOnUiThread(() -> displayFullUserInfo(user));
                }
            }
            @Override
            public void onError(Exception error) {
                Logger.e("Error loading detailed user data: " + error.getMessage());
            }
        });
    }

    private void displayBasicUserInfo(FirebaseUser user) {
        if (tvDisplayName != null) {
            String name = user.getDisplayName();
            if (name == null || name.isEmpty()) {
                name = user.getEmail();
                if (name != null && name.contains("@")) {
                    name = name.split("@")[0];
                }
            }
            tvDisplayName.setText(name != null ? name : "User");
        }
        if (tvEmail != null) {
            tvEmail.setText(user.getEmail() != null ? user.getEmail() : "");
        }
        if (imgAvatar != null) {
            if (user.getPhotoUrl() != null) {
                ImageLoader.loadCircle(activity, user.getPhotoUrl().toString(), imgAvatar);
            } else {
                imgAvatar.setImageResource(R.drawable.ic_profile);
            }
        }
    }

    private void displayFullUserInfo(User user) {
        if (user == null) return;
        if (tvDisplayName != null && user.getDisplayName() != null && !user.getDisplayName().isEmpty()) {
            tvDisplayName.setText(user.getDisplayName());
        }
        if (imgAvatar != null && user.getPhotoUrl() != null && !user.getPhotoUrl().isEmpty()) {
            ImageLoader.loadCircle(activity, user.getPhotoUrl(), imgAvatar);
        }
    }

    private void displayDefaultInfo() {
        if (tvDisplayName != null) tvDisplayName.setText("Guest User");
        if (tvEmail != null) tvEmail.setText("guest@musicapp.com");
        if (imgAvatar != null) imgAvatar.setImageResource(R.drawable.ic_profile);
    }
}