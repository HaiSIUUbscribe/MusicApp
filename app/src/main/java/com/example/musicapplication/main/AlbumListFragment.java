package com.example.musicapplication.main;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapplication.R;
import com.example.musicapplication.adapter.AlbumAdapter;
import com.example.musicapplication.model.Album;

import java.util.ArrayList;
import java.util.List;

public class AlbumListFragment extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_albums);
        RecyclerView rv = findViewById(R.id.recycler_albums);
        rv.setLayoutManager(new LinearLayoutManager(this));

        // placeholder sample data
        List<Album> albums = new ArrayList<>();
        albums.add(new Album(1, "Album One", "Artist A"));
        albums.add(new Album(2, "Album Two", "Artist B"));
        albums.add(new Album(3, "Album Three", "Artist C"));

        rv.setAdapter(new AlbumAdapter(this, albums));
    }
}

