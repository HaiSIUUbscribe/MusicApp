package com.example.musicapplication.ui.activity.upload.handlers;

import android.app.Activity;
import android.widget.EditText;
import com.example.musicapplication.R;
import com.example.musicapplication.utils.ToastUtils;
import com.example.musicapplication.utils.ValidationUtils;
import java.util.ArrayList;
import java.util.List;

public class UploadValidationHandler {
    private final Activity activity;
    private final EditText etTitle, etArtist, etAlbum, etTags;

    public UploadValidationHandler(Activity activity) {
        this.activity = activity;
        this.etTitle = activity.findViewById(R.id.et_title);
        this.etArtist = activity.findViewById(R.id.et_artist);
        this.etAlbum = activity.findViewById(R.id.et_album);
        this.etTags = activity.findViewById(R.id.et_tags);
    }

    public boolean validate(byte[] audioBytes) {
        String title = etTitle.getText().toString().trim();
        String artist = etArtist.getText().toString().trim();

        if (!ValidationUtils.isValidSongTitle(title)) {
            etTitle.setError(ValidationUtils.getSongTitleError(title));
            etTitle.requestFocus();
            return false;
        }

        if (!ValidationUtils.isNotEmpty(artist)) {
            etArtist.setError("Vui lòng nhập tên nghệ sĩ");
            etArtist.requestFocus();
            return false;
        }

        if (audioBytes == null || audioBytes.length == 0) {
            ToastUtils.showWarning(activity, "Vui lòng chọn file nhạc");
            return false;
        }
        return true;
    }

    public String getTitle() { return etTitle.getText().toString().trim(); }
    public String getArtist() { return etArtist.getText().toString().trim(); }
    public String getAlbum() { return etAlbum.getText().toString().trim(); }

    public List<String> getTags() {
        String tagsText = etTags.getText().toString().trim();
        List<String> tagsList = new ArrayList<>();
        if (!tagsText.isEmpty()) {
            String[] tagArray = tagsText.split(",");
            for (String tag : tagArray) {
                String trimmedTag = tag.trim();
                if (ValidationUtils.isNotEmpty(trimmedTag)) {
                    tagsList.add(trimmedTag);
                }
            }
        }
        return tagsList;
    }
}