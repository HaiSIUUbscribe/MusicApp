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
import com.example.musicapplication.R;
import com.example.musicapplication.main.SongListFragment;
import com.example.musicapplication.model.Album;

import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {
    private final List<Album> albums;
    private final Context context;

    public AlbumAdapter(Context context, List<Album> albums) {
        this.context = context;
        this.albums = albums;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Album a = albums.get(position);
        holder.title.setText(a.title);
        holder.artist.setText(a.artist != null ? a.artist : "Unknown");

        // Load image using resource id directly with Glide
        if (a.artResId != 0) {
            Glide.with(context)
                    .load(a.artResId)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.image);
        } else if (a.id > 0) {
            // Fallback: try MediaStore album art URI if we have an album id
            try {
                Uri albumArtUri = Uri.parse("content://media/external/audio/albumart/" + a.id);
                Glide.with(context)
                        .load(albumArtUri)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .error(R.drawable.ic_launcher_foreground)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.image);
            } catch (Exception ignored) {
                holder.image.setImageResource(R.drawable.ic_launcher_foreground);
            }
        } else {
            holder.image.setImageResource(R.drawable.ic_launcher_foreground);
        }

        holder.itemView.setOnClickListener(v -> {
            // For now open SongListActivity (could filter by album in future)
            Intent i = new Intent(context, SongListFragment.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, artist;
        ImageView image;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.item_album_title);
            artist = itemView.findViewById(R.id.item_album_artist);
            image = itemView.findViewById(R.id.imageView3);
        }
    }
}
