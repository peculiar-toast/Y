package com.alececco.y.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alececco.y.components.PostMapper;
import com.alececco.y.dto.post.CreatePostDTO;
import com.alececco.y.dto.post.PostDTO;
import com.alececco.y.dto.post.UpdatePostDTO;
import com.alececco.y.exception.PostNotFoundException;
import com.alececco.y.models.Post;
import com.alececco.y.models.Users;
import com.alececco.y.repository.PostRepository;
import com.alececco.y.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository usersRepository;
    private final AudioService audioService;
    private final PostMapper postMapper;

    /**
     * ========================
     * Public API
     * ========================
     */

    @Override
    public Optional<PostDTO> getPostById(Long id) {
        return postRepository.findById(id)
                .map(postMapper::toDto);
    }

    @Override
    public List<PostDTO> getAllPosts() {
        return postRepository.findAll().stream()
                .map(postMapper::toDto)
                .toList();
    }

    // TODO validate title before creating post
    @Transactional
    @Override
    public PostDTO createPost(CreatePostDTO dto) {
        validateTitle(dto.title());
        validateAudioFiles(dto.files());

        if (dto.userId() == null)
            throw new EntityNotFoundException("User ID is null");

        Post post = new Post();
        post.setTitle(dto.title());

        // Checks if ID actually maps to existing user
        Users user = usersRepository.findById(dto.userId()).orElseThrow(
                () -> new EntityNotFoundException("User with ID " + dto.userId().toString() + " not found"));
        post.setUser(user);

        // Save binary data and associate to post
        audioService.addAudios(post, dto.files());

        Post created = postRepository.save(post);

        return postMapper.toDto(created);
    }

    @Transactional
    @Override
    public PostDTO updatePost(Long id, UpdatePostDTO dto) {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Post not found with id: " + id));

        if (dto.title() != null) {
            validateTitle(dto.title());
            post.setTitle(dto.title());
        }

        if (dto.newAudios() != null) {
            validateAudioFiles(dto.newAudios());
            audioService.addAudios(post, dto.newAudios());
        }

        if (dto.removeAudioIds() != null) {
            validateDeleteIds(dto.removeAudioIds());
            post.getAudioMetadata()
                    .removeIf(audioData -> dto.removeAudioIds().contains(audioData.getId()));
        }

        postRepository.save(post);

        return postMapper.toDto(post);
    }

    @Transactional
    @Override
    public void deletePost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(id));

        audioService.deleteAllForPost(post);

        postRepository.deleteById(id);
    }

    /**
     * ========================
     * Internal methods
     * ========================
     */

    private void validateTitle(String title) {
        if (title.isBlank())
            throw new IllegalArgumentException("Title cannot be blank");
    }

    private void validateAudioFiles(List<MultipartFile> files) {
        if (files == null || files.contains(null))
            throw new IllegalArgumentException("Files cannot be null");
    }

    private void validateDeleteIds(List<Long> ids) {
        if (ids.contains(null))
            throw new IllegalArgumentException("Deleted ids cannot be null");
    }
}
