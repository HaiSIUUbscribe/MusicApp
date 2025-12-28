package com.example.musicapplication.player;

import com.example.musicapplication.model.Song;

import java.util.ArrayList;
import java.util.List;

public class PlaylistManager {
    private static PlaylistManager instance;
    private List<Song> playlist;
    private int currentPosition = 0;

    private PlaylistManager() {
        playlist = new ArrayList<>();
    }

    public static synchronized PlaylistManager getInstance() {
        if (instance == null) {
            instance = new PlaylistManager();
        }
        return instance;
    }

    public void setPlaylist(List<Song> songs) {
        this.playlist = new ArrayList<>(songs);
        this.currentPosition = 0;
    }

    public void setPlaylist(List<Song> songs, int position) {
        this.playlist = new ArrayList<>(songs);
        this.currentPosition = position;
    }

    public List<Song> getPlaylist() {
        return playlist;
    }

    public Song getSongAt(int index) {
        if (index >= 0 && index < playlist.size()) {
            return playlist.get(index);
        }
        return null;
    }

    public int getPlaylistSize() {
        return playlist.size();
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int position) {
        this.currentPosition = position;
    }
}
