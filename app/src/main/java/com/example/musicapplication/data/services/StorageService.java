package com.example.musicapplication.data.services;

public interface StorageService {
    interface OnUploadListener {
        void onSuccess(String url);
        void onError(Exception error);
    }
    
    interface OnDeleteListener {
        void onSuccess();
        void onError(Exception error);
    }
    
    void uploadAudio(String songId, byte[] audioData, OnUploadListener listener);
    void uploadImage(String songId, byte[] imageData, OnUploadListener listener);
    void deleteFile(String path, OnDeleteListener listener);
    String getFileUrl(String fileId);
}

