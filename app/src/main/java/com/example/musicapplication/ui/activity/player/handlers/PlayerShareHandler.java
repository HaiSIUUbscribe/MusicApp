package com.example.musicapplication.ui.activity.player.handlers;

import android.content.Intent;

import com.example.musicapplication.ui.activity.player.PlayerActivity;

public class PlayerShareHandler {
    private final PlayerActivity activity;

    public PlayerShareHandler(PlayerActivity activity) {
        this.activity = activity;
    }

    public void shareSong(String title, String artist, String url) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String shareBody = "Nghe bài hát cực hay này nhé: " + title + " - " + artist + "\n" + url;
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Chia sẻ bài hát");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        activity.startActivity(Intent.createChooser(shareIntent, "Chia sẻ qua"));
    }
}