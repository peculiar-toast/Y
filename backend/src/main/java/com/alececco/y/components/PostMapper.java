package com.alececco.y.components;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.alececco.y.dto.audio.AudioDTO;
import com.alececco.y.dto.post.PostDTO;
import com.alececco.y.models.Post;
import com.alececco.y.models.Users;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PostMapper {

    private final ModelMapper modelMapper;

    public PostDTO toDto(Post post) {
        PostDTO postDTO = new PostDTO();

        List<AudioDTO> audio = post.getAudioMetadata().stream()
                .map(audioData -> modelMapper.map(audioData, AudioDTO.class))
                .collect(Collectors.toList());
        postDTO.setAudio(audio);

        Users user = post.getUser();
        postDTO.setUserId(user.getId());
        postDTO.setUsername(user.getUsername());

        return postDTO;
    }
}
