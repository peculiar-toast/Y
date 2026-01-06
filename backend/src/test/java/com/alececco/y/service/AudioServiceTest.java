package com.alececco.y.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.alececco.y.exception.EmptyFileException;
import com.alececco.y.exception.WrongFileTypeException;
import com.alececco.y.models.AudioMetadata;
import com.alececco.y.models.Post;
import com.alececco.y.repository.AudioStorage;
import com.alececco.y.testfactory.AudioMetadataTestFactory;
import com.alececco.y.testfactory.MultipartFileTestFactory;
import com.alececco.y.testfactory.PostTestFactory;

@ExtendWith(MockitoExtension.class)
public class AudioServiceTest {

    @Mock
    AudioStorage storage;

    @InjectMocks
    AudioService audioService;

    Post post;

    @Captor
    ArgumentCaptor<AudioMetadata> metadataCaptor;

    @BeforeEach
    void setUp() {
        post = PostTestFactory.validPost();
    }

    @Test
    void addAudios_validAudio_storesSuccessfully() {
        List<MultipartFile> audios = MultipartFileTestFactory.multiple(2);
        audioService.addAudios(post, audios);

        verify(storage, times(2)).store(anyString(), any(InputStream.class));

        assertEquals(2, post.getAudioMetadata().size());

        List<AudioMetadata> saved = metadataCaptor.getAllValues();
        assertEquals(audios.getLast().getOriginalFilename(), saved.getLast().getFilename());
        assertSame(post, saved.getLast().getPost());
    }

    @Test
    void addAudios_nullFilename_throwsException() {
        MultipartFile audio = MultipartFileTestFactory.custom(
                "file",
                null,
                "audio/mpeg",
                "content".getBytes());

        assertThrows(IllegalArgumentException.class,
                () -> audioService.addAudios(post, List.of(audio)));

        verifyNoInteractions(storage);
        assertTrue(post.getAudioMetadata().isEmpty());
    }

    @Test
    void addAudios_emptyFile_throwsException() {
        MultipartFile file = new MockMultipartFile("file1", "file.exe", "audio/mpeg", "".getBytes());

        assertThrows(EmptyFileException.class,
                () -> audioService.addAudios(post, List.of(file)));

        verifyNoInteractions(storage);
        assertTrue(post.getAudioMetadata().isEmpty());
    }

    @Test
    void addAudios_wrongFileType_throwsException() {
        MultipartFile file = new MockMultipartFile("file1", "file.exe", "wrong/type", "content1".getBytes());

        assertThrows(WrongFileTypeException.class,
                () -> audioService.addAudios(post, List.of(file)));

        verifyNoInteractions(storage);
        assertTrue(post.getAudioMetadata().isEmpty());
    }

    @Test
    void delete_existingAudio_removesFromPostAndDeleteFile() {
        AudioMetadata metadata = AudioMetadataTestFactory.valid(post);
        post.getAudioMetadata().add(metadata);

        audioService.delete(post, metadata);

        verify(storage).delete(metadata.getStorageKey());
        assertTrue(post.getAudioMetadata().isEmpty());
    }

    @Test
    void delete_audioNotBelongingToPost_throwsException() {
        AudioMetadata metadata = AudioMetadataTestFactory.valid(PostTestFactory.validPost());

        assertThrows(IllegalArgumentException.class,
                () -> audioService.delete(post, metadata));

        verifyNoInteractions(storage);
    }

    @Test
    void deleteAllForPost_removesAllAudios() {
        Post postWithAudios = PostTestFactory.withAudios(3);

        audioService.deleteAllForPost(postWithAudios);

        postWithAudios.getAudioMetadata().forEach(
                a -> verify(storage).delete(a.getStorageKey()));

        assertTrue(postWithAudios.getAudioMetadata().isEmpty());
    }

    @Test
    void addAudios_storageFails_doesNotMutatePost() throws Exception {
        MultipartFile audio = MultipartFileTestFactory.single("file");

        doThrow(new IOException()).when(storage).store(anyString(), any(InputStream.class));

        assertThrows(UncheckedIOException.class,
                () -> audioService.addAudios(post, List.of(audio)));

        assertTrue(post.getAudioMetadata().isEmpty());
    }
}
