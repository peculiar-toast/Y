package com.alececco.y.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alececco.y.models.AudioMetadata;
import com.alececco.y.models.Post;
import com.alececco.y.repository.AudioMetadataRepository;
import com.alececco.y.repository.AudioStorage;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AudioService {

    private final AudioMetadataRepository metadataRepository;
    private final AudioStorage storage;

    @Transactional
    public void uploadAudio(Post post, String filename, byte[] data) {
        storage.store(filename, data);

        AudioMetadata metadata = AudioMetadata.builder()
                .filename(filename)
                .post(post)
                .build();
        metadataRepository.save(metadata);
        post.getAudioMetadata().add(metadata);
    }

    public List<AudioMetadata> getAudiosForPost(Post post) {
        return metadataRepository.findByPost(post);
    }

    public byte[] getAudio(String filename) {
        return storage.load(filename);
    }

    @Transactional
    public void deleteAudio(AudioMetadata metadata) {
        storage.delete(metadata.getFilename());

        metadata.getPost().getAudioMetadata().remove(metadata);
        metadataRepository.delete(metadata);
    }
}
