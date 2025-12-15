package com.alececco.y.service;

import org.springframework.stereotype.Service;

import com.alececco.y.models.AudioFile;

@Service
public class AudioStorageServiceImpl implements AudioStorageService {

    @Override
    public String store(AudioFile audioFile) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'store'");
    }


    @Override
    public AudioFile load(String filename) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'load'");
    }

    @Override
    public void remove(String name) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'remove'");
    }

}
