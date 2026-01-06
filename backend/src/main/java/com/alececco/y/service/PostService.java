package com.alececco.y.service;

import java.util.List;
import java.util.Optional;

import com.alececco.y.dto.post.CreatePostDTO;
import com.alececco.y.dto.post.PostDTO;
import com.alececco.y.dto.post.UpdatePostDTO;

public interface PostService {

    Optional<PostDTO> getPostById(Long id);

    List<PostDTO> getAllPosts();

    PostDTO createPost(CreatePostDTO postDTO);

    PostDTO updatePost(Long id, UpdatePostDTO updatePostDTO);

    void deletePost(Long id);
}
