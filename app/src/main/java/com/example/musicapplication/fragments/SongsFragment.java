package com.example.musicapplication.fragments;

import android.Manifest;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapplication.R;
import com.example.musicapplication.adapter.SongAdapter;
import com.example.musicapplication.model.Song;

import java.util.ArrayList;
import java.util.List;

public class SongsFragment extends Fragment {
    private static final String TAG = "SongsFragment";
    private static final int REQ_PERM = 1001;
    private RecyclerView recyclerView;
    private Button btnRefresh;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_songs, container, false);

        recyclerView = view.findViewById(R.id.recycler_songs);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        btnRefresh = view.findViewById(R.id.btn_refresh_songs);
        btnRefresh.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Đang quét file nhạc...", Toast.LENGTH_SHORT).show();
            scanMediaFiles();
        });

        if (!hasPermission()) {
            requestPermission();
        } else {
            scanMediaFiles();
        }

        return view;
    }

    private boolean hasPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_MEDIA_AUDIO}, REQ_PERM);
        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQ_PERM);
        }
    }

    private void scanMediaFiles() {
        String[] paths = {
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(),
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath(),
            "/sdcard/Download",
            "/sdcard/Music"
        };

        MediaScannerConnection.scanFile(getContext(), paths, null, (path, uri) -> {
            Log.d(TAG, "Scanned: " + path + " -> " + uri);
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> loadSongs());
            }
        });
    }

    private void loadSongs() {
        new Thread(() -> {
            List<Song> songs = new ArrayList<>();
            String[] projection = new String[]{
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.ALBUM_ID // Add ALBUM_ID to get album art
            };

            try {
                Cursor cursor = requireContext().getContentResolver().query(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        projection,
                        null,
                        null,
                        MediaStore.Audio.Media.TITLE + " ASC"
                );

                if (cursor != null) {
                    Log.d(TAG, "Total audio files found in MediaStore: " + cursor.getCount());

                    int idIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
                    int titleIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
                    int artistIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
                    int dataIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                    int albumIdIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);

                    while (cursor.moveToNext()) {
                        long id = cursor.getLong(idIndex);
                        String title = cursor.getString(titleIndex);
                        String artist = cursor.getString(artistIndex);
                        String path = cursor.getString(dataIndex);
                        long albumId = cursor.getLong(albumIdIndex);
                        String uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id).toString();

                        Log.d(TAG, "Song found: " + title + " | Artist: " + artist + " | AlbumId: " + albumId + " | Path: " + path);
                        songs.add(new Song(id, title, artist, uri, albumId));
                    }
                    cursor.close();
                } else {
                    Log.e(TAG, "Cursor is null - cannot query MediaStore");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading songs: " + e.getMessage());
                e.printStackTrace();
            }

            final int songCount = songs.size();
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (songCount == 0) {
                        Toast.makeText(getContext(), "Không tìm thấy bài hát!\nHãy thêm file MP3 vào Download/Music và nhấn 'Quét lại'", Toast.LENGTH_LONG).show();
                        Log.w(TAG, "No songs loaded - MediaStore is empty");
                    } else {
                        Toast.makeText(getContext(), "Tìm thấy " + songCount + " bài hát", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Successfully loaded " + songCount + " songs");
                    }
                    recyclerView.setAdapter(new SongAdapter(getContext(), songs));
                });
            }
        }).start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_PERM) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadSongs();
            } else {
                Toast.makeText(getContext(), "Permission is required to load songs", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
