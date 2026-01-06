package com.alececco.y.repository;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class FileSystemAudioStorage implements AudioStorage {

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(FileSystemAudioStorage.class);

    private final Path root;

    public FileSystemAudioStorage(
            @Value("${file-storage.audio}") Path storagePath) {
        this.root = storagePath;
    }

    private Path handleResolution(String filename) {
        Path resolved = root.resolve(filename).normalize();

        if (!resolved.startsWith(root)) {
            throw new IllegalArgumentException("Invalid filename");
        }

        return resolved;
    }

    @Override
    public void store(String filename, InputStream data) {
        try {
            Files.createDirectories(root);
            Path filePath = handleResolution(filename);
            Files.copy(data, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            logger.error("Error storing file '{}': {}", filename, e);
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Existence of file pointed by resource is not guaranteed
     */
    @Override
    public Resource load(String filename) {
        Path resolved = handleResolution(filename);
        return new FileSystemResource(resolved);
    }

    @Override
    public void delete(String filename) {
        Path filePath = handleResolution(filename);

        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            logger.error("Error deleting file '{}': {}", filePath, e);
            throw new UncheckedIOException(e);
        }
    }

}
