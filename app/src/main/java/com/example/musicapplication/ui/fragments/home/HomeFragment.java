package com.example.musicapplication.ui.fragments.home;

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
import com.example.musicapplication.data.repository.AlbumRepository;
import com.example.musicapplication.data.repository.ArtistRepository;
import com.example.musicapplication.data.repository.AuthRepository;
import com.example.musicapplication.data.repository.SongRepository;
import com.example.musicapplication.data.repository.UserRepository;
import com.example.musicapplication.model.Artist;
import com.example.musicapplication.model.Song;
import com.example.musicapplication.player.MusicPlayer;
import com.example.musicapplication.player.PlaylistManager;
import com.example.musicapplication.ui.activity.player.PlayerActivity;
import com.example.musicapplication.ui.activity.ArtistDetailActivity;
import com.example.musicapplication.ui.fragments.home.handlers.HomeAlbumsHandler;
import com.example.musicapplication.ui.fragments.home.handlers.HomeArtistsHandler;
import com.example.musicapplication.ui.fragments.home.handlers.HomeNewSongsHandler;
import com.example.musicapplication.ui.fragments.home.handlers.HomePopularHandler;
import com.example.musicapplication.ui.fragments.home.handlers.HomeProfileHandler;
import com.example.musicapplication.ui.fragments.home.handlers.HomeSliderHandler;

import java.util.List;

public class HomeFragment extends Fragment {

    private ProgressBar progressBar;

    // Repositories
    private SongRepository songRepository;
    private AlbumRepository albumRepository;
    private ArtistRepository artistRepository;
    private AuthRepository authRepository;
    private UserRepository userRepository;

    // Handlers
    private HomeSliderHandler sliderHandler;
    private HomeAlbumsHandler albumsHandler;
    private HomeArtistsHandler artistsHandler;
    private HomePopularHandler popularHandler;
    private HomeNewSongsHandler newSongsHandler;
    private HomeProfileHandler profileHandler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initRepositories();
        initViews(view);
        initHandlers(view);

        loadData();
        return view;
    }

    private void initRepositories() {
        songRepository = new SongRepository(getContext());
        albumRepository = new AlbumRepository(getContext());
        artistRepository = new ArtistRepository(getContext());
        authRepository = new AuthRepository(getContext());
        userRepository = new UserRepository(getContext());
    }

    private void initViews(View view) {
        progressBar = view.findViewById(R.id.progress_bar);
    }
    private void initHandlers(View view) {
        sliderHandler = new HomeSliderHandler(view);
        albumsHandler = new HomeAlbumsHandler(getContext(), view, albumRepository, songRepository);
        artistsHandler = new HomeArtistsHandler(getContext(), view, artistRepository, this::onArtistClick);
        popularHandler = new HomePopularHandler(getContext(), view, songRepository, this::onSongClick);
        newSongsHandler = new HomeNewSongsHandler(getContext(), view, songRepository, this::onSongClick);
        profileHandler = new HomeProfileHandler(getContext(), view, userRepository);
    }

    private void loadData() {
        if (!authRepository.isLoggedIn()) {
            setLoading(false);
            return;
        }
        setLoading(true);
        
        // Counter để đếm số handlers đã load xong
        final int[] completedHandlers = {0};
        final int totalHandlers = 4; // albums, artists, popular, newSongs (không đếm profile và slider)
        
        Runnable checkCompletion = () -> {
            completedHandlers[0]++;
            if (completedHandlers[0] >= totalHandlers) {
                setLoading(false);
            }
        };
        
        // Delegate loading logic to handlers với callback
        albumsHandler.loadData(checkCompletion);
        artistsHandler.loadData(checkCompletion);
        popularHandler.loadData(checkCompletion);
        newSongsHandler.loadData(checkCompletion);
        profileHandler.loadUserProfile();
    }
    private void onSongClick(Song song, int position, List<Song> playlist) {
        PlaylistManager.getInstance().setPlaylist(playlist, position);
        MusicPlayer.getInstance(getContext()).setPlaylist(playlist, position);
        if (song.isOnline() && song.getId() != null) songRepository.incrementPlayCount(song.getId());

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

    private void onArtistClick(Artist artist) {
        Intent intent = new Intent(getContext(), ArtistDetailActivity.class);
        intent.putExtra(ArtistDetailActivity.EXTRA_ARTIST_ID, artist.getId());
        intent.putExtra(ArtistDetailActivity.EXTRA_ARTIST_NAME, artist.getName());
        intent.putExtra(ArtistDetailActivity.EXTRA_ARTIST_IMAGE, artist.getImageUrl());
        intent.putExtra(ArtistDetailActivity.EXTRA_ARTIST_FOLLOWERS, artist.getFollowers());
        startActivity(intent);
    }

    private void setLoading(boolean loading) {
        if (progressBar != null) progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sliderHandler != null) sliderHandler.onPause();
    }
    @Override
    public void onResume() {
        super.onResume();
        if (sliderHandler != null) sliderHandler.onResume();
        if (profileHandler != null) profileHandler.loadUserProfile();
    }
}