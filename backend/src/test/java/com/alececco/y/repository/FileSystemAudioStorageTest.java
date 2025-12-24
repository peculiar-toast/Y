package com.alececco.y.repository;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.alececco.y.exception.EmptyFileException;
import com.alececco.y.exception.WrongFileTypeException;

public class FileSystemAudioStorageTest {

    FileSystemAudioStorage audioStorage;

    @BeforeEach
    void setUp(@TempDir Path tempPath) {
        audioStorage = new FileSystemAudioStorage(tempPath);
    }

    @Test
    void store_validFilenameAndData_success() {
        String filename = "valid_filename.mp3";
        byte[] data = "valid data".getBytes();

        audioStorage.store(filename, data);

        byte[] loaded = audioStorage.load(filename);
        assertArrayEquals(data, loaded);
    }

    @Test
    void store_emptyFilename_throwsException() {
        String filename = "";
        byte[] data = "some data".getBytes();

        assertThrows(IllegalArgumentException.class, () -> {
            audioStorage.store(filename, data);
        });
    }

    @Test
    void store_invalidFilename_throwsException() {
        String filename = "invalid_filename.txt";
        byte[] data = "some data".getBytes();

        assertThrows(WrongFileTypeException.class, () -> {
            audioStorage.store(filename, data);
        });
    }

    @Test
    void store_emptyData_throwsException() {
        String filename = "valid_filename.mp3";
        byte[] data = new byte[0];

        assertThrows(EmptyFileException.class, () -> {
            audioStorage.store(filename, data);
        });
    }

    @Test
    public void load_existentFile_returnsData() {
        String filename = "existent_file.mp3";
        byte[] data = "existent data".getBytes();

        audioStorage.store(filename, data);

        byte[] loadedData = audioStorage.load(filename);
        assertArrayEquals(data, loadedData);
    }

    @Test
    public void load_nonExistentFile_throwsException() {
        String filename = "non_existent_file.mp3";
     
        assertThrows(RuntimeException.class, () -> {
            audioStorage.load(filename);
        });
    }

    @Test
    public void delete_existentFile_fileIsDeleted() {
        String filename = "file_to_delete.mp3";
        byte[] data = "data to delete".getBytes();
        audioStorage.store(filename, data);
        audioStorage.delete(filename);

        assertThrows(RuntimeException.class, () -> {
            audioStorage.load(filename);
        });
    }
}
