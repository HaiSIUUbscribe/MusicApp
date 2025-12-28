package com.example.musicapplication.ui.fragments.home.handlers;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.musicapplication.R;
import com.example.musicapplication.data.repository.AlbumRepository;
import com.example.musicapplication.data.repository.SongRepository;
import com.example.musicapplication.model.Album;
import com.example.musicapplication.model.Song;
import com.example.musicapplication.ui.activity.album.AlbumDetailActivity;
import com.example.musicapplication.ui.activity.album.AllAlbumsActivity;
import com.example.musicapplication.ui.adapter.AlbumAdapter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HomeAlbumsHandler {
    private final Context context;
    private final AlbumRepository albumRepository;
    private final SongRepository songRepository;

    private RecyclerView recyclerAlbums;
    private TextView tvSeeAllAlbums;
    private AlbumAdapter albumAdapter;
    private final List<Album> albumList = new ArrayList<>();

    public HomeAlbumsHandler(Context context, View view, AlbumRepository albumRepo, SongRepository songRepo) {
        this.context = context;
        this.albumRepository = albumRepo;
        this.songRepository = songRepo;
        initViews(view);
    }

    private void initViews(View view) {
        recyclerAlbums = view.findViewById(R.id.recycler_albums);
        tvSeeAllAlbums = view.findViewById(R.id.tv_see_all_albums);

        recyclerAlbums.setLayoutManager(new GridLayoutManager(context, 2));
        recyclerAlbums.setNestedScrollingEnabled(false);

        albumAdapter = new AlbumAdapter(context, albumList, album -> {
            Intent intent = new Intent(context, AlbumDetailActivity.class);
            intent.putExtra("albumName", album.getTitle());
            intent.putExtra("albumImage", album.getCoverUrl());
            context.startActivity(intent);
        });
        recyclerAlbums.setAdapter(albumAdapter);

        tvSeeAllAlbums.setOnClickListener(v -> {
            Intent intent = new Intent(context, AllAlbumsActivity.class);
            context.startActivity(intent);
        });
    }

    private Runnable loadCompleteCallback;

    public void loadData() {
        loadData(null);
    }

    public void loadData(Runnable onComplete) {
        this.loadCompleteCallback = onComplete;
        albumRepository.getAlbums(new AlbumRepository.OnResultListener<List<Album>>() {
            @Override
            public void onSuccess(List<Album> result) {
                if (result != null && !result.isEmpty()) {
                    processAlbumsForHome(result);
                } else {
                    loadAlbumsFromSongs(); // Fallback
                }
            }
            @Override
            public void onError(Exception error) {
                loadAlbumsFromSongs(); // Fallback
            }
        });
    }

    private void processAlbumsForHome(List<Album> fullList) {
        albumList.clear();
        int limit = 4;
        for (int i = 0; i < fullList.size() && i < limit; i++) {
            albumList.add(fullList.get(i));
        }
        notifyAdapter();
    }

    private void loadAlbumsFromSongs() {
        songRepository.getTrendingSongs(50, new SongRepository.OnResultListener<List<Song>>() {
            @Override
            public void onSuccess(List<Song> result) {
                List<Album> tempAlbums = new ArrayList<>();
                Set<String> added = new HashSet<>();
                for (Song song : result) {
                    String name = song.getAlbum();
                    if (name != null && !added.contains(name)) {
                        tempAlbums.add(new Album(song.getId(), name, song.getArtist(), song.getImageUrl()));
                        added.add(name);
                    }
                }
                processAlbumsForHome(tempAlbums);
            }
            @Override public void onError(Exception e) { notifyAdapter(); }
        });
    }

    private void notifyAdapter() {
        if (recyclerAlbums != null) {
            recyclerAlbums.post(() -> albumAdapter.notifyDataSetChanged());
        }
        if (loadCompleteCallback != null) {
            loadCompleteCallback.run();
            loadCompleteCallback = null;
        }
    }
}