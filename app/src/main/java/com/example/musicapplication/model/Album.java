package com.example.musicapplication.model;

import androidx.annotation.NonNull;

public class Album {
    public long id;
    public String title;
    public String artist;
    // Changed: use int resource id instead of URI string for drawable resources
    public int artResId;

    public Album(long id, String title, String artist) {
        this(id, title, artist, 0);
    }

    public Album(long id, String title, String artist, int artResId) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.artResId = artResId;
    }

    @NonNull
    @Override
    public String toString() {
        return title + (artist != null && !artist.isEmpty() ? " - " + artist : "");
    }
}
