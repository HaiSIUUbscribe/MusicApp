package com.example.musicapplication.ui.activity.player.handlers;

import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import com.example.musicapplication.data.repository.PlaylistRepository;
import com.example.musicapplication.model.Playlist;
import com.example.musicapplication.ui.activity.player.PlayerActivity;
import com.example.musicapplication.utils.ToastUtils;
import com.google.firebase.firestore.ListenerRegistration;
import java.util.ArrayList;
import java.util.List;

public class PlayerPlaylistHandler {
    private final PlayerActivity activity;
    private final PlaylistRepository playlistRepository;
    private final List<Playlist> userPlaylists = new ArrayList<>();
    private ListenerRegistration playlistListener;

    public PlayerPlaylistHandler(PlayerActivity activity, PlaylistRepository repository) {
        this.activity = activity;
        this.playlistRepository = repository;
    }

    public void startListening() {
        playlistListener = playlistRepository.getRealtimeUserPlaylists(new PlaylistRepository.OnResultListener<List<Playlist>>() {
            @Override
            public void onSuccess(List<Playlist> result) {
                userPlaylists.clear();
                userPlaylists.addAll(result);
            }
            @Override
            public void onError(Exception error) {}
        });
    }

    public void stopListening() {
        if (playlistListener != null) {
            playlistListener.remove();
        }
    }

    public void showAddToPlaylistDialog(String songId) {
        if (songId == null) {
            ToastUtils.showWarning(activity, "Không thể thêm bài hát này");
            return;
        }

        String[] playlistNames = new String[userPlaylists.size()];
        for (int i = 0; i < userPlaylists.size(); i++) {
            playlistNames[i] = userPlaylists.get(i).getName();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Chọn Playlist");

        if (playlistNames.length > 0) {
            builder.setItems(playlistNames, (dialog, which) -> {
                Playlist selectedPlaylist = userPlaylists.get(which);
                addSongToPlaylist(selectedPlaylist, songId);
            });
        } else {
            builder.setMessage("Bạn chưa có Playlist nào.");
        }

        builder.setPositiveButton("Tạo Playlist Mới", (dialog, which) -> showCreatePlaylistDialog());
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void addSongToPlaylist(Playlist playlist, String songId) {
        playlistRepository.addSongToPlaylist(playlist.getId(), songId, new PlaylistRepository.OnResultListener<Void>() {
            @Override
            public void onSuccess(Void result) {
                ToastUtils.showSuccess(activity, "Đã thêm vào " + playlist.getName());
            }
            @Override
            public void onError(Exception error) {
                ToastUtils.showError(activity, error.getMessage());
            }
        });
    }

    private void showCreatePlaylistDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Tạo Playlist Mới");
        final EditText input = new EditText(activity);
        input.setHint("Tên Playlist");
        builder.setView(input);

        builder.setPositiveButton("Tạo", (dialog, which) -> {
            String name = input.getText().toString();
            if (!name.isEmpty()) {
                playlistRepository.createPlaylist(name, new PlaylistRepository.OnResultListener<String>() {
                    @Override
                    public void onSuccess(String playlistId) {
                        ToastUtils.showSuccess(activity, "Đã tạo Playlist!");
                    }
                    @Override
                    public void onError(Exception error) {
                        ToastUtils.showError(activity, "Lỗi tạo Playlist");
                    }
                });
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }
}