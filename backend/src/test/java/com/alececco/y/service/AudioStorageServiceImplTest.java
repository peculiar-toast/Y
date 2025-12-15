package com.alececco.y.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.alececco.y.exception.EmptyFileException;
import com.alececco.y.exception.WrongFileTypeException;
import com.alececco.y.models.AudioFile;

// @RequiredArgsConstructor
// @ExtendWith(MockitoExtension.class)
public class AudioStorageServiceImplTest {
    
    private AudioStorageServiceImpl audioStorageService;

    @BeforeAll
    void setUp() {
        audioStorageService = new AudioStorageServiceImpl();
    }

    @Test
    void store_emptyFile_throwsException() {
        AudioFile emptyFile = null;

        assertThrows(EmptyFileException.class, () -> {
            audioStorageService.store(emptyFile);
        });
    }

    @Test
    void store_wrongFileType_throwsException() {
        String filename = "test-audio.txt";
        byte[] content = "dummy content".getBytes();
        AudioFile wrongFile = AudioFile.builder()
            .filename(filename)
            .data(content)
            .build();

        assertThrows(WrongFileTypeException.class, () -> {
            audioStorageService.store(wrongFile);
        });
    }

    @Test
    void store_validFile_storesSuccessfully() {
        String filename = "test-audio.mp3";
        byte[] content = "dummy audio content".getBytes();
        AudioFile validFile = AudioFile.builder()
            .filename(filename)
            .data(content)
            .build();

        audioStorageService.store(validFile);

        AudioFile foundAudio = audioStorageService.load(filename);
        assertNotNull(foundAudio);
        assertEquals(filename, foundAudio.getFilename());
    }

    @Test
    void load_existingFile_returnsFile() {
        String filename = "existing-audio.mp3";
        AudioFile loadedFile = audioStorageService.load(filename);

        assertNotNull(loadedFile);
        assertEquals(filename, loadedFile.getFilename());
    }

    @Test
    void load_nonExistingFile_throwsException() {
        String filename = "non-existing-audio.mp3";

        assertThrows(RuntimeException.class, () -> {
            audioStorageService.load(filename);
        });
    }

    @Test
    void remove_existingFile_removesSuccessfully() {
        String filename = "audio-to-remove.mp3";
        audioStorageService.remove(filename);

        assertThrows(RuntimeException.class, () -> {
            audioStorageService.load(filename);
        });
    }
}
