package com.alececco.y.repository;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.alececco.y.controller.PostController;
import com.alececco.y.exception.EmptyFileException;
import com.alececco.y.exception.WrongFileTypeException;

@Repository
public class FileSystemAudioStorage implements AudioStorage {

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(PostController.class);

    private final Path root;

    public FileSystemAudioStorage(
            @Value("${file-storage.audio}") Path storagePath) {
        this.root = storagePath;
    }

    private void validateFilename(String filename, byte[] data) {
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("Filename cannot be empty!");
        }

        // TODO add more audio formats
        if (!filename.endsWith(".mp3") && !filename.endsWith(".wav")) {
            throw new WrongFileTypeException("File must be audio");
        }

        if (data == null || data.length == 0) {
            throw new EmptyFileException("File cannot be empty");
        }
    }

    @Override
    public void store(String filename, byte[] data) {
        validateFilename(filename, data);

        Path filePath = root.resolve(filename);
        try {
            Files.createDirectories(root.getParent());
            Files.write(filePath, data);
        } catch (IOException e) {
            logger.error("Error storing file '{}': {}", filePath, e.getMessage());
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public byte[] load(String filename) {
        Path filePath = root.resolve(filename);

        if (!Files.exists(filePath)) {
            logger.error("File '{}' not found", filePath);
            throw new RuntimeException("File '" + filePath + "' not found");
        }

        try {
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            logger.error("Error loading file '{}': {}", filePath, e.getMessage());
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void delete(String filename) {
        Path filePath = root.resolve(filename);

        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            logger.error("Error deleting file '{}': {}", filePath, e.getMessage());
            throw new UncheckedIOException(e);
        }
    }

}
