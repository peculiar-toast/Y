package com.alececco.y.repository;

import java.util.List;
import java.util.Optional;

import com.alececco.y.models.AudioData;

public interface AudioDataRepository {
    
    Optional<AudioData> findByPostId(Long postId);

    Optional<AudioData> findByFilename(String filename);

    void store(AudioData audioData);

    void storeAll(List<AudioData> audioDataList);

    void update(AudioData audioData);

    void deleteByIds(List<Long> ids);

    void deleteByPostId(Long postId);

    void deleteByFilename(String filename);
}
