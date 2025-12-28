package com.example.musicapplication.ui.fragments.home.handlers;

import android.content.Context;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.musicapplication.R;
import com.example.musicapplication.data.repository.SongRepository;
import com.example.musicapplication.model.Song;
import com.example.musicapplication.ui.adapter.SongListAdapter;
import java.util.ArrayList;
import java.util.List;

public class HomePopularHandler {
    private final SongRepository songRepository;
    private RecyclerView recyclerPopular;
    private SongListAdapter popularAdapter;
    private final List<Song> popularList = new ArrayList<>();

    public HomePopularHandler(Context context, View view, SongRepository repo, SongListAdapter.OnSongClickListener listener) {
        this.songRepository = repo;
        recyclerPopular = view.findViewById(R.id.recycler_popular);
        recyclerPopular.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        popularAdapter = new SongListAdapter(context, popularList, listener);
        recyclerPopular.setAdapter(popularAdapter);
    }

    public void loadData() {
        loadData(null);
    }

    public void loadData(Runnable onComplete) {
        songRepository.getTrendingSongs(10, new SongRepository.OnResultListener<List<Song>>() {
            @Override
            public void onSuccess(List<Song> result) {
                popularList.clear();
                popularList.addAll(result);
                if (recyclerPopular != null) {
                    recyclerPopular.post(() -> popularAdapter.notifyDataSetChanged());
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