package com.alececco.y.dto.audio;

import org.springframework.web.multipart.MultipartFile;

public record CreateAudioDTO(
    MultipartFile file
) {
}