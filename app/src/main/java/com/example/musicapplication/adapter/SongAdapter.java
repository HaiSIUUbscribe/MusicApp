package com.example.musicapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.musicapplication.main.PlayerFragment;
import com.example.musicapplication.R;
import com.example.musicapplication.model.Song;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {
    private final List<Song> songs;
    private final Context context;

    public SongAdapter(Context context, List<Song> songs) {
        this.context = context;
        this.songs = songs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song s = songs.get(position);
        holder.title.setText(s.title);
        holder.artist.setText(s.artist != null ? s.artist : "Unknown");

        // Load album art using Glide (more reliable than setImageURI)
        if (s.albumId > 0) {
            try {
                Uri albumArtUri = Uri.parse("content://media/external/audio/albumart/" + s.albumId);
                Glide.with(context)
                        .load(albumArtUri)
                        .placeholder(R.drawable.ic_music)
                        .error(R.drawable.ic_music)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .into(holder.image);
            } catch (Exception e) {
                Glide.with(context)
                        .load(R.drawable.ic_music)
                        .into(holder.image);
            }
        } else {
            Glide.with(context)
                    .load(R.drawable.ic_music)
                    .into(holder.image);
        }

        holder.itemView.setOnClickListener(view -> {
            Intent i = new Intent(context, PlayerFragment.class);
            i.putExtra("uri", s.uri);
            i.putExtra("title", s.title);
            i.putExtra("artist", s.artist);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, artist;
        ImageView image;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.item_song_title);
            artist = itemView.findViewById(R.id.item_song_artist);
            image = itemView.findViewById(R.id.imageView);
        }
    }
}
