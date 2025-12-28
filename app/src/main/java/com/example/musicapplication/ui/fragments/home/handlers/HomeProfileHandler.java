package com.example.musicapplication.ui.fragments.home.handlers;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import com.example.musicapplication.R;
import com.example.musicapplication.data.repository.UserRepository;
import com.example.musicapplication.model.User;
import com.example.musicapplication.utils.ImageLoader;
import com.example.musicapplication.utils.Logger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeProfileHandler {
    private final Context context;
    private final UserRepository userRepository;
    private ImageView imgProfile;

    public HomeProfileHandler(Context context, View view, UserRepository userRepo) {
        this.context = context;
        this.userRepository = userRepo;
        this.imgProfile = view.findViewById(R.id.img_profile);
    }

    public void loadUserProfile() {
        if (imgProfile == null) return;
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            userRepository.getUser(currentUser.getUid(), new UserRepository.OnResultListener<User>() {
                @Override
                public void onSuccess(User user) {
                    if (user != null && user.getPhotoUrl() != null && !user.getPhotoUrl().isEmpty()) {
                        imgProfile.post(() -> ImageLoader.loadCircle(context, user.getPhotoUrl(), imgProfile));
                    }
                }
                @Override
                public void onError(Exception error) {
                    Logger.e("Error loading user profile", error);
                }
            });
        } else {
            imgProfile.setImageResource(R.drawable.ic_profile);
        }
    }
}