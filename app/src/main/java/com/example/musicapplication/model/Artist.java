package com.example.musicapplication.model;

public class Artist {
    private String id;
    private String name;
    private String imageUrl;
    private long followers;
    private long songCount;
    private String genre;

    public Artist() {
        // Required empty constructor for Firestore
    }

    public Artist(String id, String name, String imageUrl) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.followers = 0;
        this.songCount = 0;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getFollowers() {
        return followers;
    }

    public void setFollowers(long followers) {
        this.followers = followers;
    }

    public long getSongCount() {
        return songCount;
    }

    public void setSongCount(long songCount) {
        this.songCount = songCount;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
}
