package com.example.musicapplication.main;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapplication.player.MusicPlayer;
import com.example.musicapplication.R;

public class PlayerFragment extends AppCompatActivity {
    private MusicPlayer player;
    private String uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        TextView title = findViewById(R.id.player_title);
        TextView artist = findViewById(R.id.player_artist);
        Button btnPlay = findViewById(R.id.btn_play);
        Button btnPause = findViewById(R.id.btn_pause);
        Button btnStop = findViewById(R.id.btn_stop);

        uri = getIntent().getStringExtra("uri");
        String t = getIntent().getStringExtra("title");
        String a = getIntent().getStringExtra("artist");
        title.setText(t != null ? t : "Unknown");
        artist.setText(a != null ? a : "");

        player = MusicPlayer.getInstance(this);

        btnPlay.setOnClickListener(v -> player.play(uri));
        btnPause.setOnClickListener(v -> player.pause());
        btnStop.setOnClickListener(v -> player.stop());
    }
}
