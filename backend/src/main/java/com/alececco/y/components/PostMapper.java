package com.alececco.y.components;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.alececco.y.dto.audio.AudioDTO;
import com.alececco.y.dto.post.PostDTO;
import com.alececco.y.models.Post;
import com.alececco.y.models.Users;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PostMapper {

    private final AudioMapper audioMapper;

    public PostDTO toDto(Post post) {
        PostDTO postDTO = new PostDTO();

        postDTO.setId(post.getId());

        postDTO.setTitle(post.getTitle());

        List<AudioDTO> audio = post.getAudioMetadata().stream()
                .map(audioData -> audioMapper.toDto(audioData))
                .collect(Collectors.toList());
        postDTO.setAudio(audio);

        Users user = post.getUser();
        postDTO.setUserId(user.getId());
        postDTO.setUsername(user.getUsername());

        return postDTO;
    }
}
