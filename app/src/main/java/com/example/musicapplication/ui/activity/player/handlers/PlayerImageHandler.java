package com.example.musicapplication.ui.activity.player.handlers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;
import com.example.musicapplication.R;
import com.example.musicapplication.ui.activity.player.PlayerActivity;
import com.example.musicapplication.utils.ImageLoader;

public class PlayerImageHandler {
    private final PlayerActivity activity;
    private ImageView albumArt;
    private View playerRoot;

    public PlayerImageHandler(PlayerActivity activity) {
        this.activity = activity;
        initViews();
    }

    private void initViews() {
        albumArt = activity.findViewById(R.id.player_album_art);
        playerRoot = activity.findViewById(R.id.player_root);
    }

    public void loadCoverImage(boolean isOnline, String imageUrl, String audioUrl) {
        if (isOnline && imageUrl != null && !imageUrl.isEmpty()) {
            ImageLoader.loadWithCallback(activity, imageUrl, albumArt, this::applyGradientFromBitmap);
        } else if (audioUrl != null) {
            loadAlbumArtFromMetadata(audioUrl);
        } else {
            albumArt.setImageResource(R.drawable.ic_music); // Default
        }
    }

    private void loadAlbumArtFromMetadata(String path) {
        new Thread(() -> {
            try {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(activity, Uri.parse(path));
                byte[] art = retriever.getEmbeddedPicture();
                retriever.release();
                if (art != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
                    activity.runOnUiThread(() -> {
                        albumArt.setImageBitmap(bitmap);
                        applyGradientFromBitmap(bitmap);
                    });
                } else {
                    activity.runOnUiThread(() -> albumArt.setImageResource(R.drawable.ic_music));
                }
            } catch (Exception e) {
                activity.runOnUiThread(() -> albumArt.setImageResource(R.drawable.ic_music));
            }
        }).start();
    }

    private void applyGradientFromBitmap(Bitmap bitmap) {
        Palette.from(bitmap).generate(palette -> {
            int dominant = palette.getDominantColor(ContextCompat.getColor(activity, R.color.background_dark));
            int vibrant = palette.getVibrantColor(dominant);
            int darkVibrant = palette.getDarkVibrantColor(dominant);

            GradientDrawable gradient = new GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    new int[]{vibrant, darkVibrant, ContextCompat.getColor(activity, R.color.background_dark)}
            );
            gradient.setCornerRadius(0f);
            playerRoot.setBackground(gradient);
        });
    }
}