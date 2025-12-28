package com.example.musicapplication.ui.fragments.library.handlers;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import com.example.musicapplication.R;
import com.google.android.material.textfield.TextInputEditText;

public class LibrarySearchHandler {
    private final TextInputEditText etSearch;
    private final LibraryPlaylistHandler playlistHandler;
    private final LibraryLikedHandler likedHandler;
    private final LibraryHistoryHandler historyHandler;

    public LibrarySearchHandler(View view, LibraryPlaylistHandler playlistHandler, LibraryLikedHandler likedHandler, LibraryHistoryHandler historyHandler) {
        this.etSearch = view.findViewById(R.id.et_library_search);
        this.playlistHandler = playlistHandler;
        this.likedHandler = likedHandler;
        this.historyHandler = historyHandler;

        setupSearchListener();
    }

    private void setupSearchListener() {
        if (etSearch == null) return;

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString();
                playlistHandler.filter(query);
                likedHandler.filter(query);
                historyHandler.filter(query);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
}