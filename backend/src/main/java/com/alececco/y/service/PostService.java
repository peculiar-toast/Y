package com.alececco.y.service;

import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.alececco.y.dto.post.CreatePostDTO;
import com.alececco.y.dto.post.PostDTO;
import com.alececco.y.dto.post.UpdatePostDTO;

public interface PostService {
    
    Optional<PostDTO> getPostById(Long id);

    List<PostDTO> getAllPosts();

    void createPost(CreatePostDTO postDTO);

    void updatePost(Long id, UpdatePostDTO updatePostDTO);

    void deletePost(Long id);
}
