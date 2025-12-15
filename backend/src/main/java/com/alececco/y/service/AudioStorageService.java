package com.alececco.y.service;

import com.alececco.y.models.AudioFile;

public interface AudioStorageService {
    String store(AudioFile audioFile);
    AudioFile load(String name);
    void remove(String name);
}
