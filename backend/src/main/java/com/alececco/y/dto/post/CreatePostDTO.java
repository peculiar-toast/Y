package com.alececco.y.dto.post;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreatePostDTO(
        @NotNull @NotBlank String title,
        @NotNull List<@NotNull MultipartFile> files,
        @NotNull Long userId) {
}
