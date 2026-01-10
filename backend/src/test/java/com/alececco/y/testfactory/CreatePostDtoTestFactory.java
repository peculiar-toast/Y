package com.alececco.y.testfactory;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.alececco.y.dto.post.CreatePostDTO;

public class CreatePostDtoTestFactory {

    private CreatePostDtoTestFactory() {
    }

    public static CreatePostDTO valid() {
        return new CreatePostDTO("title", List.of(), 1L);
    }

    public static CreatePostDTO single(String title, int files, Long userId) {
        return new CreatePostDTO(
                title,
                MultipartFileTestFactory.multiple(files),
                userId);
    }

    public static CreatePostDTO custom(String title, List<MultipartFile> files, Long userId) {
        return new CreatePostDTO(title, files, userId);
    }
}
