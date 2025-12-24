package com.alececco.y.dto.post;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdatePostDTO (

        @NotBlank(message = "Title cannot be blank")
        String title,

        @NotNull List<@NotNull MultipartFile> newAudios,
        @NotNull List<@NotNull Long> removeAudioIds) {
}
