package com.alececco.y.dto.post;

import java.util.List;

import com.alececco.y.dto.audio.CreateAudioDTO;

import lombok.Data;

@Data
public class UpdatePostDTO {
    Long id;
    String title;
    
    List<CreateAudioDTO> newAudios;
    List<Long> removeAudioIds;
}
