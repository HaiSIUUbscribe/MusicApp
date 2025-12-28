package com.example.musicapplication.ui.fragments.library;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.musicapplication.R;
import com.example.musicapplication.data.repository.FavoriteRepository;
import com.example.musicapplication.data.repository.HistoryRepository;
import com.example.musicapplication.data.repository.PlaylistRepository;
import com.example.musicapplication.data.repository.SongRepository;
import com.example.musicapplication.data.repository.UserRepository;
import com.example.musicapplication.model.Song;
import com.example.musicapplication.player.MusicPlayer;
import com.example.musicapplication.player.PlaylistManager;
import com.example.musicapplication.ui.activity.player.PlayerActivity;
import com.example.musicapplication.ui.activity.upload.UploadSongActivity;
import com.example.musicapplication.ui.fragments.library.handlers.LibraryHistoryHandler;
import com.example.musicapplication.ui.fragments.library.handlers.LibraryLikedHandler;
import com.example.musicapplication.ui.fragments.library.handlers.LibraryPlaylistHandler;
import com.example.musicapplication.ui.fragments.library.handlers.LibraryProfileHandler;
import com.example.musicapplication.ui.fragments.library.handlers.LibrarySearchHandler;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class LibraryFragment extends Fragment {

    private ProgressBar progressBar;
    private MaterialButton btnUpload;

    // Repositories
    private SongRepository songRepository;
    private FavoriteRepository favoriteRepository;
    private PlaylistRepository playlistRepository;
    private HistoryRepository historyRepository;
    private UserRepository userRepository;

    // Handlers
    private LibraryProfileHandler profileHandler;
    private LibraryPlaylistHandler playlistHandler;
    private LibraryLikedHandler likedHandler;
    private LibraryHistoryHandler historyHandler;
    private LibrarySearchHandler searchHandler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);

        initRepositories();
        initViews(view);
        initHandlers(view);
        loadData();

        return view;
    }

    private void initRepositories() {
        Context context = getContext();
        songRepository = new SongRepository(context);
        favoriteRepository = new FavoriteRepository(context);
        playlistRepository = new PlaylistRepository(context);
        historyRepository = new HistoryRepository(context);
        userRepository = new UserRepository(context);
    }

    private void initViews(View view) {
        progressBar = view.findViewById(R.id.progress_bar);
        btnUpload = view.findViewById(R.id.btn_upload);

        btnUpload.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), UploadSongActivity.class);
            startActivity(intent);
        });
    }

    private void initHandlers(View view) {
        Context context = getContext();

        profileHandler = new LibraryProfileHandler(context, view, userRepository);

        playlistHandler = new LibraryPlaylistHandler(context, view, playlistRepository);

        // Pass onSongClick callback to Handlers
        likedHandler = new LibraryLikedHandler(context, view, favoriteRepository, this::onSongClick);
        historyHandler = new LibraryHistoryHandler(context, view, historyRepository, this::onSongClick);

        // Search handler needs access to other handlers to filter them
        searchHandler = new LibrarySearchHandler(view, playlistHandler, likedHandler, historyHandler);
    }

    private void loadData() {
        setLoading(true);
        
        // Counter để đếm số handlers đã load xong
        final int[] completedHandlers = {0};
        final int totalHandlers = 3; // playlists, liked, history
        
        Runnable checkCompletion = () -> {
            completedHandlers[0]++;
            if (completedHandlers[0] >= totalHandlers) {
                setLoading(false);
            }
        };
        
        profileHandler.loadUserProfile();
        playlistHandler.startListening(checkCompletion);
        likedHandler.startListening(checkCompletion);
        historyHandler.startListening(checkCompletion);
    }

    private void onSongClick(Song song, int position, List<Song> playlist) {
        PlaylistManager.getInstance().setPlaylist(playlist, position);
        MusicPlayer.getInstance(getContext()).setPlaylist(playlist, position);

        if (song.isOnline() && song.getId() != null) {
            songRepository.incrementPlayCount(song.getId());
        }
        if (historyRepository != null) {
            historyRepository.addToHistory(song.getId(), null);
        }

        Intent intent = new Intent(getContext(), PlayerActivity.class);
        intent.putExtra("songId", song.getId());
        intent.putExtra("title", song.getTitle());
        intent.putExtra("artist", song.getArtist());
        intent.putExtra("imageUrl", song.getImageUrl());
        intent.putExtra("audioUrl", song.getAudioUrl());
        intent.putExtra("songIndex", position);
        intent.putExtra("isOnline", song.isOnline());
        startActivity(intent);
    }

    private void setLoading(boolean loading) {
        if (progressBar != null) {
            progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (playlistHandler != null) playlistHandler.stopListening();
        if (likedHandler != null) likedHandler.stopListening();
        if (historyHandler != null) historyHandler.stopListening();
    }
}