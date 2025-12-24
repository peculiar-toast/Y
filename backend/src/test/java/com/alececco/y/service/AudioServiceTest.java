package com.alececco.y.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.alececco.y.exception.EmptyFileException;
import com.alececco.y.exception.WrongFileTypeException;
import com.alececco.y.models.AudioMetadata;
import com.alececco.y.models.Post;
import com.alececco.y.repository.AudioMetadataRepository;
import com.alececco.y.repository.AudioStorage;

@ExtendWith(MockitoExtension.class)
public class AudioServiceTest {

    @Mock
    AudioStorage storage;

    @Mock
    AudioMetadataRepository metadataRepository;

    @InjectMocks
    AudioService audioService;

    Post post;

    @Captor
    ArgumentCaptor<AudioMetadata> metadataCaptor;

    @BeforeEach
    void setUp() {
        post = Post.builder()
                .id(1L)
                .title("Test Post")
                .build();
        post.setAudioMetadata(new ArrayList<>());
    }

    @Test
    void uploadAudio_validAudio_storesSuccessfully() {
        String filename = "test-audio.mp3";
        byte[] content = "dummy audio content".getBytes();

        audioService.uploadAudio(post, filename, content);

        verify(storage).store(filename, content);
        verify(metadataRepository).save(metadataCaptor.capture());

        AudioMetadata savedMetadata = metadataCaptor.getValue();
        assertNotNull(savedMetadata);
        assertEquals(filename, savedMetadata.getFilename());
        assertEquals(post, savedMetadata.getPost());

        assertEquals(1, post.getAudioMetadata().size());
        assertEquals(filename, post.getAudioMetadata().get(0).getFilename());
    }

    @Test
    void uploadAudio_nullFilename_throwsException() {
        String filename = null;
        byte[] content = "some content".getBytes();

        doThrow(new IllegalArgumentException("Filename cannot be empty!")).when(storage).store(eq(filename), any());

        assertThrows(IllegalArgumentException.class, () -> {
            audioService.uploadAudio(post, filename, content);
        });

        verifyNoInteractions(metadataRepository);
    }

    @Test
    void uploadAudio_emptyFile_throwsException() {
        String filename = "empty-audio.mp3";
        byte[] content = new byte[0];

        doThrow(new EmptyFileException("File cannot be empty")).when(storage).store(eq(filename), eq(content));

        assertThrows(EmptyFileException.class, () -> {
            audioService.uploadAudio(post, filename, content);
        });

        verifyNoInteractions(metadataRepository);
    }

    @Test
    void uploadAudio_wrongFileType_throwsException() {
        String filename = "wrong-audio.txt";
        byte[] content = "some content".getBytes();

        doThrow(new WrongFileTypeException("Wrong filetype")).when(storage).store(eq(filename), eq(content));

        assertThrows(WrongFileTypeException.class, () -> {
            audioService.uploadAudio(post, filename, content);
        });

        verifyNoInteractions(metadataRepository);
    }

    @Test
    void getAudiosForPost_existingPost_returnsMetadataList() {
        AudioMetadata metadata1 = AudioMetadata.builder().filename("audio1.mp3").post(post).build();
        AudioMetadata metadata2 = AudioMetadata.builder().filename("audio2.mp3").post(post).build();

        when(metadataRepository.findByPost(post)).thenReturn(List.of(metadata1, metadata2));

        var result = audioService.getAudiosForPost(post);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void deleteAudio_existingMetadata_deletesSuccessfully() {
        AudioMetadata metadata = AudioMetadata.builder()
                .filename("to-delete-audio.mp3")
                .post(post)
                .build();
        post.getAudioMetadata().add(metadata);

        audioService.deleteAudio(metadata);

        verify(storage).delete(metadata.getFilename());
        verify(metadataRepository).delete(metadata);
        assertEquals(0, post.getAudioMetadata().size());
    }

    @Test
    void deleteAudio_nonExistingMetadata_throwsException() {
        AudioMetadata metadata = AudioMetadata.builder()
                .filename("non-existing-audio.mp3")
                .post(post)
                .build();

        doThrow(new RuntimeException("File not found")).when(storage).delete(metadata.getFilename());

        assertThrows(RuntimeException.class, () -> {
            audioService.deleteAudio(metadata);
        });

        verify(storage).delete(metadata.getFilename());
        verifyNoInteractions(metadataRepository);
    }
}
