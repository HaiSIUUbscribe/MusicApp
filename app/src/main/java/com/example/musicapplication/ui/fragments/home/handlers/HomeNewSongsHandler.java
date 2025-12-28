package com.example.musicapplication.ui.fragments.home.handlers;

import android.content.Context;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.musicapplication.R;
import com.example.musicapplication.data.repository.SongRepository;
import com.example.musicapplication.model.Song;
import com.example.musicapplication.ui.adapter.SongAdapter;
import java.util.ArrayList;
import java.util.List;

public class HomeNewSongsHandler {
    private final SongRepository songRepository;
    private RecyclerView recyclerNewSongs;
    private SongAdapter newSongAdapter;
    private final List<Song> newList = new ArrayList<>();

    public HomeNewSongsHandler(Context context, View view, SongRepository repo, SongAdapter.OnSongClickListener listener) {
        this.songRepository = repo;
        recyclerNewSongs = view.findViewById(R.id.recycler_new_songs);
        recyclerNewSongs.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        newSongAdapter = new SongAdapter(context, newList, listener);
        recyclerNewSongs.setAdapter(newSongAdapter);
    }

    public void loadData() {
        loadData(null);
    }

    public void loadData(Runnable onComplete) {
        songRepository.getRecentlyAddedSongs(10, new SongRepository.OnResultListener<List<Song>>() {
            @Override
            public void onSuccess(List<Song> result) {
                newList.clear();
                newList.addAll(result);
                if (recyclerNewSongs != null) {
                    recyclerNewSongs.post(() -> newSongAdapter.notifyDataSetChanged());
                }
                if (onComplete != null) onComplete.run();
            }
            @Override
            public void onError(Exception e) {
                if (onComplete != null) onComplete.run();
            }
        });
    }
}