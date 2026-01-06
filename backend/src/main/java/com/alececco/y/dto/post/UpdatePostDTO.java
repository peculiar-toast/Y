package com.alececco.y.dto.post;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UpdatePostDTO(
        @Pattern(regexp = ".*\\S.*", message = "Title cannot be blank") String title,
        List<@NotNull MultipartFile> newAudios,
        List<@NotNull Long> removeAudioIds) {
}
