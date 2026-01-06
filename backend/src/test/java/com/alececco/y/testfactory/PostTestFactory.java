package com.alececco.y.testfactory;

import java.util.ArrayList;
import java.util.List;

import com.alececco.y.models.AudioMetadata;
import com.alececco.y.models.Post;

public final class PostTestFactory {

    private PostTestFactory() {
    }

    public static Post validPost() {
        return Post.builder()
                .title("Default title")
                .audioMetadata(new ArrayList<>())
                .user(UserTestFactory.validUser())
                .build();
    }

    public static Post withTitle(String title) {
        return validPost().toBuilder()
                .title(title)
                .build();
    }

    public static Post withAudios(int count) {
        Post post = validPost();
        List<AudioMetadata> audios = AudioMetadataTestFactory.multiple(post, count);
        post.getAudioMetadata().addAll(audios);
        return post;
    }

    public static Post custom(String title, List<AudioMetadata> audios) {
        Post post = Post.builder()
                .title(title)
                .audioMetadata(audios)
                .user(UserTestFactory.validUser())
                .build();

        if (audios != null) {
            audios.forEach(audio -> audio.setPost(post));
            post.getAudioMetadata().addAll(audios);
        }

        return post;
    }
}
