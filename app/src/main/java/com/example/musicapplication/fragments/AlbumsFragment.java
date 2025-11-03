package com.example.musicapplication.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapplication.R;
import com.example.musicapplication.adapter.AlbumAdapter;
import com.example.musicapplication.model.Album;

import java.util.ArrayList;
import java.util.List;

public class AlbumsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_albums, container, false);

        RecyclerView rv = view.findViewById(R.id.recycler_albums);
        // Use GridLayoutManager with 2 columns for better space efficiency
        rv.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Use the three drawable resource ids you added
        List<Album> albums = new ArrayList<>();
        albums.add(new Album(1, "Sơn Tùng M-TP", "Artist A", R.drawable.sontungmtp));
        albums.add(new Album(2, "Tóc Tiên", "Artist B", R.drawable.toctien));
        albums.add(new Album(3, "Hiếu Thứ Hai", "Artist C", R.drawable.hieuthuhai));

        rv.setAdapter(new AlbumAdapter(getContext(), albums));

        return view;
    }
}
