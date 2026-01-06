package com.alececco.y.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alececco.y.dto.post.CreatePostDTO;
import com.alececco.y.dto.post.PostDTO;
import com.alececco.y.dto.post.UpdatePostDTO;
import com.alececco.y.service.PostService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable Long id) {
        return postService.getPostById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<PostDTO>> getAllPosts() {
        List<PostDTO> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    @PostMapping
    public ResponseEntity<String> createPost(
            @ModelAttribute @Valid CreatePostDTO dto) {
        postService.createPost(dto);
        return ResponseEntity.ok("Post created succesfully");
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<String> updatePost(
            @PathVariable Long id,
            @ModelAttribute @Valid UpdatePostDTO dto) {
        postService.updatePost(id, dto);
        return ResponseEntity.ok("Post updated succesfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}
