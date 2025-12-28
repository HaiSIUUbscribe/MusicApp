package com.example.musicapplication.data.services;

import android.content.Context;
import com.example.musicapplication.utils.Logger;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class FirebaseStorageService implements StorageService {
    private FirebaseStorage storage;
    private Context context;
    
    public FirebaseStorageService(Context context) {
        this.context = context.getApplicationContext();
        this.storage = FirebaseStorage.getInstance();
    }
    
    @Override
    public void uploadAudio(String songId, byte[] audioData, OnUploadListener listener) {
        StorageReference audioRef = storage.getReference().child("songs").child(songId + ".mp3");
        UploadTask uploadTask = audioRef.putBytes(audioData);
        
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            audioRef.getDownloadUrl().addOnSuccessListener(uri -> {
                Logger.d("Audio uploaded successfully: " + uri.toString());
                listener.onSuccess(uri.toString());
            }).addOnFailureListener(e -> {
                Logger.e("Error getting audio URL", e);
                listener.onError(e);
            });
        }).addOnFailureListener(e -> {
            Logger.e("Error uploading audio", e);
            listener.onError(e);
        });
    }
    
    @Override
    public void uploadImage(String songId, byte[] imageData, OnUploadListener listener) {
        StorageReference imageRef = storage.getReference().child("images").child(songId + ".jpg");
        UploadTask uploadTask = imageRef.putBytes(imageData);
        
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                Logger.d("Image uploaded successfully: " + uri.toString());
                listener.onSuccess(uri.toString());
            }).addOnFailureListener(e -> {
                Logger.e( "Error getting image URL", e);
                listener.onError(e);
            });
        }).addOnFailureListener(e -> {
            Logger.e("Error uploading image", e);
            listener.onError(e);
        });
    }
    
    @Override
    public void deleteFile(String path, OnDeleteListener listener) {
        StorageReference fileRef = storage.getReference().child(path);
        fileRef.delete()
            .addOnSuccessListener(aVoid -> {
                Logger.d("File deleted successfully: " + path);
                listener.onSuccess();
            })
            .addOnFailureListener(e -> {
                Logger.e("Error deleting file", e);
                listener.onError(e);
            });
    }
    
    @Override
    public String getFileUrl(String fileId) {
        // Firebase Storage URLs are generated on upload, return null here
        // Use getDownloadUrl() from StorageReference instead
        return null;
    }
}

