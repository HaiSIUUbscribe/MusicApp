package com.example.musicapplication.ui.activity.album;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapplication.R;
import com.example.musicapplication.ui.adapter.AlbumAdapter;
import com.example.musicapplication.data.repository.AlbumRepository;
import com.example.musicapplication.data.repository.SongRepository;
import com.example.musicapplication.model.Song;
import com.example.musicapplication.model.Album;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AllAlbumsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AlbumAdapter albumAdapter;
    private List<Album> albumList = new ArrayList<>();
    private SongRepository songRepository;
    private AlbumRepository albumRepository;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_albums);

        initViews();
        loadAllAlbums();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        progressBar = findViewById(R.id.progress_bar);
        recyclerView = findViewById(R.id.recycler_all_albums);

        // Grid 2 cột
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        albumAdapter = new AlbumAdapter(this, albumList, album -> {
            Intent intent = new Intent(this, AlbumDetailActivity.class);
            intent.putExtra("albumName", album.getTitle());
            intent.putExtra("albumImage", album.getCoverUrl());
            startActivity(intent);
        });
        recyclerView.setAdapter(albumAdapter);

        songRepository = new SongRepository(this);
        albumRepository = new AlbumRepository(this);
    }

    private void loadAllAlbums() {
        setLoading(true);

        // 1. Thử lấy từ Collection Albums
        albumRepository.getAlbums(new AlbumRepository.OnResultListener<List<Album>>() {
            @Override
            public void onSuccess(List<Album> result) {
                if (result != null && !result.isEmpty()) {
                    albumList.clear();
                    albumList.addAll(result);
                    updateUI();
                } else {
                    loadAlbumsFromSongsFallback();
                }
            }

            @Override
            public void onError(Exception error) {
                loadAlbumsFromSongsFallback();
            }
        });
    }

    private void loadAlbumsFromSongsFallback() {
        // Lấy nhiều bài hát hơn để tìm được nhiều album hơn
        songRepository.getTrendingSongs(100, new SongRepository.OnResultListener<List<Song>>() {
            @Override
            public void onSuccess(List<Song> result) {
                albumList.clear();
                Set<String> added = new HashSet<>();
                for (Song song : result) {
                    String name = song.getAlbum();
                    if (name != null && !name.isEmpty() && !added.contains(name)) {
                        albumList.add(new Album(song.getId(), name, song.getArtist(), song.getImageUrl()));
                        added.add(name);
                    }
                }
                updateUI();
            }

            @Override
            public void onError(Exception e) {
                setLoading(false);
                Toast.makeText(AllAlbumsActivity.this, "Lỗi tải album", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI() {
        runOnUiThread(() -> {
            albumAdapter.notifyDataSetChanged();
            setLoading(false);
        });
    }

    private void setLoading(boolean loading) {
        if (progressBar != null) {
            progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        }
    }
}