package com.alececco.y.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.Resource;

public class FileSystemAudioStorageTest {

    FileSystemAudioStorage audioStorage;

    @BeforeEach
    void setUp(@TempDir Path tempPath) {
        audioStorage = new FileSystemAudioStorage(tempPath);
    }

    @Test
    void store_validFilenameAndData_success() throws IOException {
        String filename = "valid_filename.mp3";
        byte[] data = "valid data".getBytes();
        InputStream inputStream = new ByteArrayInputStream(data);

        audioStorage.store(filename, inputStream);

        Resource loaded = audioStorage.load(filename);
        assertArrayEquals(data, loaded.getContentAsByteArray());
    }

    @Test
    public void load_existentFile_returnsData() throws IOException {
        String filename = "existent_file.mp3";
        byte[] data = "existent data".getBytes();

        audioStorage.store(filename, new ByteArrayInputStream(data));

        Resource loadedData = audioStorage.load(filename);
        assertArrayEquals(data, loadedData.getContentAsByteArray());
    }

    @Test
    public void delete_existentFile_fileIsDeleted() {
        String filename = "file_to_delete.mp3";
        byte[] data = "data to delete".getBytes();
        audioStorage.store(filename, new ByteArrayInputStream(data));
        audioStorage.delete(filename);

        Resource deletedFile = audioStorage.load(filename);

        assertFalse(deletedFile.exists());
    }
}
