package com.alececco.y.testfactory;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import com.alececco.y.models.AudioMetadata;
import com.alececco.y.models.Post;

public final class AudioMetadataTestFactory {

    private AudioMetadataTestFactory() {
    }

    public static AudioMetadata valid(Post post) {
        return AudioMetadata.builder()
                .filename("file.mp3")
                .storageKey(UUID.randomUUID().toString())
                .contentType("audio/mpeg")
                .size(100)
                .post(post)
                .build();
    }

    public static List<AudioMetadata> multiple(Post post, int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> valid(post))
                .toList();
    }
}
