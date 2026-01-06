package com.alececco.y.components;

import org.springframework.stereotype.Component;

import com.alececco.y.dto.audio.AudioDTO;
import com.alececco.y.models.AudioMetadata;

@Component
public class AudioMapper {
    public AudioDTO toDto(AudioMetadata meta) {
        return AudioDTO.builder()
                .id(meta.getId())
                .url("audio/" + meta.getStorageKey())
                .build();
    }
}
