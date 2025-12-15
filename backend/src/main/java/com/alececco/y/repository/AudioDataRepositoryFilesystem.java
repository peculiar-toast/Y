package com.alececco.y.repository;

import java.io.File;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.alececco.y.models.AudioData;

@Repository
public class AudioDataRepositoryFilesystem implements AudioDataRepository {


    @Value("${file-storage.audio}")
    private String STORAGE_PATH;

    @Override
    public Optional<AudioData> findByFilename(String filename) {
        File file = new File(STORAGE_PATH, filename);
        if (file.exists()) {
            AudioData audioData = AudioData.builder()
                .filename(filename)
                .build();
            return Optional.of(audioData);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<AudioData> findByPostId(Long postId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void store(AudioData audioData) {
        // TODO Auto-generated method stub

    }

    @Override
    public void storeAll(List<AudioData> audioDataList) {
        // TODO Auto-generated method stub

    }

    @Override
    public void update(AudioData audioData) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        throw new UnsupportedOperationException("Unimplemented method 'deleteByIds'");

    }

    @Override
    public void deleteByFilename(String filename) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteByFilename'");

    }

    @Override
    public void deleteByPostId(Long postId) {
        // TODO Auto-generated method stub

    }

}
