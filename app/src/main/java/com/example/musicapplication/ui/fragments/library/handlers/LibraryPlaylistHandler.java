package com.example.musicapplication.ui.fragments.library.handlers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.musicapplication.R;
import com.example.musicapplication.data.repository.PlaylistRepository;
import com.example.musicapplication.model.Playlist;
import com.example.musicapplication.ui.activity.playlist.PlaylistDetailActivity;
import com.example.musicapplication.ui.adapter.PlaylistAdapter;
import com.example.musicapplication.utils.Logger;
import com.example.musicapplication.utils.ToastUtils;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.ListenerRegistration;
import java.util.ArrayList;
import java.util.List;

public class LibraryPlaylistHandler {
    private final Context context;
    private final PlaylistRepository playlistRepository;
    private final RecyclerView recyclerPlaylists;
    private final MaterialButton btnCreatePlaylist;

    private PlaylistAdapter playlistAdapter;
    private final List<Playlist> allPlaylists = new ArrayList<>();
    private final List<Playlist> displayPlaylists = new ArrayList<>();
    private ListenerRegistration playlistsListener;

    public LibraryPlaylistHandler(Context context, View view, PlaylistRepository repository) {
        this.context = context;
        this.playlistRepository = repository;
        this.recyclerPlaylists = view.findViewById(R.id.recycler_playlists);
        this.btnCreatePlaylist = view.findViewById(R.id.btn_create_playlist);

        setupRecyclerView();
        setupEvents();
    }

    private void setupRecyclerView() {
        recyclerPlaylists.setLayoutManager(new LinearLayoutManager(context));
        playlistAdapter = new PlaylistAdapter(context, displayPlaylists, playlist -> {
            Intent intent = new Intent(context, PlaylistDetailActivity.class);
            intent.putExtra("playlist", playlist);
            context.startActivity(intent);
        });
        recyclerPlaylists.setAdapter(playlistAdapter);
    }

    private void setupEvents() {
        btnCreatePlaylist.setOnClickListener(v -> showCreatePlaylistDialog());
    }

    public void startListening() {
        startListening(null);
    }

    public void startListening(Runnable onComplete) {
        playlistsListener = playlistRepository.getRealtimeUserPlaylists(new PlaylistRepository.OnResultListener<List<Playlist>>() {
            @Override
            public void onSuccess(List<Playlist> result) {
                allPlaylists.clear();
                allPlaylists.addAll(result);
                filter(""); // Refresh display list
                if (onComplete != null) onComplete.run();
            }
            @Override
            public void onError(Exception e) {
                Logger.e("Error loading playlists", e);
                if (onComplete != null) onComplete.run();
            }
        });
    }

    public void filter(String query) {
        displayPlaylists.clear();
        String lowerQuery = query.toLowerCase().trim();
        if (lowerQuery.isEmpty()) {
            displayPlaylists.addAll(allPlaylists);
        } else {
            for (Playlist p : allPlaylists) {
                if (p.getName().toLowerCase().contains(lowerQuery)) {
                    displayPlaylists.add(p);
                }
            }
        }
        playlistAdapter.updateList(displayPlaylists);
    }

    private void showCreatePlaylistDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Tạo Playlist Mới");
        final EditText input = new EditText(context);
        input.setHint("Tên Playlist");
        builder.setView(input);

        builder.setPositiveButton("Create", (dialog, which) -> {
            String name = input.getText().toString().trim();
            if (!name.isEmpty()) {
                playlistRepository.createPlaylist(name, new PlaylistRepository.OnResultListener<String>() {
                    @Override
                    public void onSuccess(String result) {
                        ToastUtils.showSuccess(context, "Đã tạo Playlist!");
                    }
                    @Override
                    public void onError(Exception e) {
                        ToastUtils.showError(context, "Failed: " + e.getMessage());
                    }
                });
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    public void stopListening() {
        if (playlistsListener != null) playlistsListener.remove();
    }
}