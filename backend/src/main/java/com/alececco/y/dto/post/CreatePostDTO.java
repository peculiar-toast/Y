package com.alececco.y.dto.post;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public record CreatePostDTO (
    String title,
    List<MultipartFile> files,
    Long userId
) {
}
