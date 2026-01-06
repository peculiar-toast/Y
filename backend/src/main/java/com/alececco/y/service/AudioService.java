package com.alececco.y.service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.alececco.y.exception.EmptyFileException;
import com.alececco.y.exception.WrongFileTypeException;
import com.alececco.y.models.AudioMetadata;
import com.alececco.y.models.Post;
import com.alececco.y.repository.AudioStorage;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AudioService {

    private final AudioStorage storage;

    private static final Set<String> ALLOWED_FILETYPES = Set.of(
            "audio/mpeg",
            "audio/wav",
            "audio/x-wav");

    /*
     * ===================
     * Public API
     * ===================
     */

    // TODO add to testing file size > 0
    @Transactional
    public void addAudios(Post post, List<MultipartFile> files) {
        for (MultipartFile file : files) {
            addSingle(post, file);
        }
    }

    @Transactional(readOnly = true)
    public Resource load(String storageKey) {
        return storage.load(storageKey);
    }

    @Transactional
    public void delete(Post post, AudioMetadata metadata) {
        if (!post.getAudioMetadata().contains(metadata)) {
            throw new IllegalArgumentException("Audio does not belong to the given post");
        }

        storage.delete(metadata.getStorageKey());

        post.getAudioMetadata().remove(metadata);
    }

    @Transactional
    public void deleteAllForPost(Post post) {
        for (AudioMetadata meta : List.copyOf(post.getAudioMetadata())) {
            storage.delete(meta.getStorageKey());
            post.getAudioMetadata().remove(meta);
        }
    }

    /*
     * ===================
     * Internal Helpers
     * ===================
     */

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new EmptyFileException("Audio file is empty");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("Missing filename!");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_FILETYPES.contains(contentType)) {
            throw new WrongFileTypeException("Unsupported audio format: " + contentType);
        }
    }

    private void addSingle(Post post, MultipartFile file) {
        validateFile(file);

        String storageKey = UUID.randomUUID().toString();

        try {
            storage.store(storageKey, file.getInputStream());
        } catch (IOException e) {
            // If one fails, all fails
            throw new UncheckedIOException(
                    "File '" + file.getOriginalFilename() + "' failed upload.", e);
        }

        // Then saves the metadata into the db
        AudioMetadata metadata = AudioMetadata.builder()
                .filename(file.getOriginalFilename())
                .storageKey(storageKey)
                .contentType(file.getContentType())
                .size(file.getSize())
                .post(post)
                .build();

        post.getAudioMetadata().add(metadata);
    }
}
