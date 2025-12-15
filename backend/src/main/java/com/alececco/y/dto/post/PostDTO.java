package com.alececco.y.dto.post;

import java.util.List;

import com.alececco.y.dto.audio.AudioDTO;

import lombok.Data;

@Data
public class PostDTO {
    Long id;
    String title;
    List<AudioDTO> audio;

    Long userId;
    String username;
}
