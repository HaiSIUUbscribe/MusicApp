package com.example.musicapplication.ui.activity.upload.handlers;

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.widget.EditText;
import android.widget.ImageView;
import com.example.musicapplication.R;
import com.example.musicapplication.utils.ImageLoader;
import com.example.musicapplication.utils.Logger;
import com.example.musicapplication.utils.ValidationUtils;
import java.io.ByteArrayOutputStream;

public class UploadMetadataHandler {
    private final Activity activity;
    private final EditText etTitle, etArtist, etAlbum;
    private final ImageView imgCover;
    private long currentDuration = 0;

    public UploadMetadataHandler(Activity activity) {
        this.activity = activity;
        this.etTitle = activity.findViewById(R.id.et_title);
        this.etArtist = activity.findViewById(R.id.et_artist);
        this.etAlbum = activity.findViewById(R.id.et_album);
        this.imgCover = activity.findViewById(R.id.img_cover);
    }

    // Trả về byte[] của ảnh nếu lấy được từ metadata để cập nhật lại cho FilePicker
    public byte[] extractAudioMetadata(Uri uri) {
        byte[] embeddedImage = null;
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(activity, uri);

            String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            String album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);

            if (ValidationUtils.isNotEmpty(title)) etTitle.setText(title);
            if (ValidationUtils.isNotEmpty(artist)) etArtist.setText(artist);
            if (ValidationUtils.isNotEmpty(album)) etAlbum.setText(album);

            String durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            currentDuration = durationStr != null ? Long.parseLong(durationStr) : 0;

            byte[] art = retriever.getEmbeddedPicture();
            if (art != null) {
                Bitmap bitmap = android.graphics.BitmapFactory.decodeByteArray(art, 0, art.length);
                imgCover.setImageBitmap(bitmap);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
                embeddedImage = stream.toByteArray();
            }

            retriever.release();
        } catch (Exception e) {
            Logger.e("Lỗi trích xuất metadata", e);
            currentDuration = 0;
        }
        return embeddedImage;
    }

    public void displayImage(Uri imageUri) {
        ImageLoader.load(activity, imageUri.toString(), imgCover);
    }

    public long getCurrentDuration() {
        return currentDuration;
    }
}