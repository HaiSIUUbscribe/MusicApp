package com.example.musicapplication.ui.fragments.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.musicapplication.R;
import com.example.musicapplication.data.repository.AuthRepository;
import com.example.musicapplication.data.repository.PlaylistRepository;
import com.example.musicapplication.data.repository.ProfileRepository;
import com.example.musicapplication.data.repository.UserRepository;
import com.example.musicapplication.ui.fragments.profile.handlers.ProfileInfoHandler;
import com.example.musicapplication.ui.fragments.profile.handlers.ProfileSettingsHandler;
import com.example.musicapplication.ui.fragments.profile.handlers.ProfileStatsHandler;

public class ProfileFragment extends Fragment {

    // Repositories
    private AuthRepository authRepository;
    private UserRepository userRepository;
    private ProfileRepository profileRepository;
    private PlaylistRepository playlistRepository;

    // Handlers
    private ProfileInfoHandler infoHandler;
    private ProfileStatsHandler statsHandler;
    private ProfileSettingsHandler settingsHandler;

    // Launcher chọn ảnh phải khai báo ở Fragment
    private final ActivityResultLauncher<String> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                // Chuyển kết quả sang SettingsHandler xử lý
                if (settingsHandler != null) {
                    settingsHandler.onImageSelected(uri);
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initRepositories();
        initHandlers(view);
        loadData();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void initRepositories() {
        authRepository = new AuthRepository(requireContext());
        userRepository = new UserRepository(requireContext());
        profileRepository = new ProfileRepository(requireContext());
        playlistRepository = new PlaylistRepository(requireContext());
    }

    private void initHandlers(View view) {
        // 1. Info Handler
        infoHandler = new ProfileInfoHandler(getActivity(), view, userRepository);

        // 2. Stats Handler
        statsHandler = new ProfileStatsHandler(getActivity(), view, userRepository, playlistRepository);

        // 3. Settings Handler
        // Callback 1: Mở bộ chọn ảnh
        // Callback 2: Khi update xong thì reload lại Info và Stats
        settingsHandler = new ProfileSettingsHandler(
                requireContext(),
                view,
                authRepository,
                profileRepository,
                () -> pickImageLauncher.launch("image/*"), // ImagePickerCallback
                this::loadData // UpdateCallback
        );
    }

    private void loadData() {
        if (infoHandler != null) infoHandler.loadUserInfo();
        if (statsHandler != null) statsHandler.loadStats();
    }
}