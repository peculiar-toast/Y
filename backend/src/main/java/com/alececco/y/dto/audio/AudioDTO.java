package com.alececco.y.dto.audio;

import org.springframework.web.multipart.MultipartFile;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AudioDTO {
    private Long id;
    private String url;
    private MultipartFile file;
}
