package com.example.musicapplication.ui.fragments.library.handlers;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.musicapplication.R;
import com.example.musicapplication.data.repository.HistoryRepository;
import com.example.musicapplication.model.Song;
import com.example.musicapplication.ui.adapter.SongListAdapter;
import com.example.musicapplication.utils.Logger;
import com.example.musicapplication.utils.ToastUtils;
import com.google.firebase.firestore.ListenerRegistration;
import java.util.ArrayList;
import java.util.List;

public class LibraryHistoryHandler {
    private final Context context;
    private final HistoryRepository historyRepository;
    private final RecyclerView recyclerHistory;
    private final TextView tvClearHistory;

    private SongListAdapter historyAdapter;
    private final List<Song> allHistorySongs = new ArrayList<>();
    private final List<Song> displayHistorySongs = new ArrayList<>();
    private ListenerRegistration historyListener;

    public LibraryHistoryHandler(Context context, View view, HistoryRepository repository, SongListAdapter.OnSongClickListener listener) {
        this.context = context;
        this.historyRepository = repository;
        this.recyclerHistory = view.findViewById(R.id.recycler_history);
        this.tvClearHistory = view.findViewById(R.id.tv_clear_history);

        setupRecyclerView(listener);
        setupEvents();
    }

    private void setupRecyclerView(SongListAdapter.OnSongClickListener listener) {
        recyclerHistory.setLayoutManager(new LinearLayoutManager(context));
        historyAdapter = new SongListAdapter(context, displayHistorySongs, listener);
        historyAdapter.setOnSongLongClickListener((song, position) -> showDeleteSingleDialog(song));
        recyclerHistory.setAdapter(historyAdapter);
    }

    private void setupEvents() {
        if (tvClearHistory != null) {
            tvClearHistory.setOnClickListener(v -> showClearAllDialog());
        }
    }

    public void startListening() {
        startListening(null);
    }

    public void startListening(Runnable onComplete) {
        historyListener = historyRepository.getRealtimeHistory(new HistoryRepository.OnResultListener<List<Song>>() {
            @Override
            public void onSuccess(List<Song> result) {
                allHistorySongs.clear();
                allHistorySongs.addAll(result);
                filter("");
                if (onComplete != null) onComplete.run();
            }
            @Override
            public void onError(Exception e) {
                Logger.e("Error loading history", e);
                if (onComplete != null) onComplete.run();
            }
        });
    }

    public void filter(String query) {
        displayHistorySongs.clear();
        String lowerQuery = query.toLowerCase().trim();
        if (lowerQuery.isEmpty()) {
            displayHistorySongs.addAll(allHistorySongs);
        } else {
            for (Song s : allHistorySongs) {
                if (s.getTitle().toLowerCase().contains(lowerQuery) || s.getArtist().toLowerCase().contains(lowerQuery)) {
                    displayHistorySongs.add(s);
                }
            }
        }
        historyAdapter.notifyDataSetChanged();
    }

    private void showDeleteSingleDialog(Song song) {
        new AlertDialog.Builder(context)
                .setTitle("Xóa khỏi lịch sử")
                .setMessage("Bạn có muốn xóa bài hát '" + song.getTitle() + "' khỏi lịch sử nghe không?")
                .setPositiveButton("Xóa", (dialog, which) -> historyRepository.removeFromHistory(song.getId(), null))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showClearAllDialog() {
        if (allHistorySongs.isEmpty()) {
            ToastUtils.showInfo(context, "Lịch sử đang trống!");
            return;
        }
        new AlertDialog.Builder(context)
                .setTitle("Xoá lịch sử")
                .setMessage("Bạn có chắc chắn muốn xoá toàn bộ lịch sử nghe nhạc không?")
                .setPositiveButton("Xoá", (dialog, which) -> historyRepository.clearHistory(null))
                .setNegativeButton("Hủy", null)
                .show();
    }

    public void stopListening() {
        if (historyListener != null) historyListener.remove();
    }
}