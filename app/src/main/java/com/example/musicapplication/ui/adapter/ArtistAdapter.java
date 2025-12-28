package com.example.musicapplication.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapplication.R;
import com.example.musicapplication.model.Artist;
import com.example.musicapplication.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder> {

    private Context context;
    private List<Artist> artists;
    private OnArtistClickListener listener;

    public interface OnArtistClickListener {
        void onArtistClick(Artist artist);
    }

    public ArtistAdapter(Context context, OnArtistClickListener listener) {
        this.context = context;
        this.artists = new ArrayList<>();
        this.listener = listener;
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists != null ? artists : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_artist, parent, false);
        return new ArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistViewHolder holder, int position) {
        Artist artist = artists.get(position);
        holder.bind(artist);
    }

    @Override
    public int getItemCount() {
        return artists.size();
    }

    class ArtistViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgArtist;
        private TextView tvArtistName;
        private TextView tvFollowers;

        public ArtistViewHolder(@NonNull View itemView) {
            super(itemView);
            imgArtist = itemView.findViewById(R.id.img_artist);
            tvArtistName = itemView.findViewById(R.id.tv_artist_name);
            tvFollowers = itemView.findViewById(R.id.tv_followers);
        }

        public void bind(Artist artist) {
            tvArtistName.setText(artist.getName());
            
            // Format followers count
            String followersText = formatFollowers(artist.getFollowers());
            tvFollowers.setText(followersText);

            // Load artist image (circular)
            if (artist.getImageUrl() != null && !artist.getImageUrl().isEmpty()) {
                ImageLoader.load(context, artist.getImageUrl(), imgArtist);
            } else {
                imgArtist.setImageResource(R.drawable.ic_profile);
            }

            // Click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onArtistClick(artist);
                }
            });
        }

        private String formatFollowers(long followers) {
            if (followers >= 1000000) {
                return String.format("%.1fM followers", followers / 1000000.0);
            } else if (followers >= 1000) {
                return String.format("%.1fK followers", followers / 1000.0);
            } else {
                return followers + " followers";
            }
        }
    }
}
