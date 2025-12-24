package com.alececco.y.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alececco.y.components.PostMapper;
import com.alececco.y.dto.post.CreatePostDTO;
import com.alececco.y.dto.post.PostDTO;
import com.alececco.y.dto.post.UpdatePostDTO;
import com.alececco.y.models.AudioMetadata;
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
    private final PostMapper postMapper;

    @Override
    public Optional<PostDTO> getPostById(Long id) {
        return postRepository.findById(id)
                .map(postMapper::toDto);
    }

    // TODO temporary: remove as soon as no longer convenient
    @Override
    public List<PostDTO> getAllPosts() {
        List<PostDTO> posts = postRepository.findAll().stream()
                .map(postMapper::toDto)
                .toList();

        return posts;
    }

    @Transactional
    @Override
    public void createPost(CreatePostDTO postDTO) {
        Post post = new Post();
        post.setTitle(postDTO.title());

        Users user = usersRepository.findById(postDTO.userId()).orElseThrow(
                () -> new EntityNotFoundException("User with ID " + postDTO.userId().toString() + " not found"));
        post.setUser(user);

        List<AudioMetadata> audioDataList = postDTO.files().stream()
                .map(audioDTO -> {
                    AudioMetadata audioData = AudioMetadata.builder()
                            .filename(audioDTO.getName())
                            .post(post)
                            .build();
                    return audioData;
                })
                .collect(Collectors.toList());
        post.setAudioMetadata(audioDataList);

        postRepository.save(post);
    }

    @Transactional
    @Override
    public void updatePost(Long id, UpdatePostDTO updatePostDTO) {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Post not found with id: " + id));

        if (updatePostDTO.title() != null) {
            if (updatePostDTO.title().isBlank()) {
                throw new IllegalArgumentException("Title cannot be blank");
            }
            post.setTitle(updatePostDTO.title());
        }

        if (updatePostDTO.newAudios().isEmpty() == false) {
            List<AudioMetadata> newAudioDataList = updatePostDTO.newAudios().stream()
                    .map(audioDTO -> {
                        AudioMetadata audioData = AudioMetadata.builder()
                                .filename(audioDTO.getName())
                                .post(post)
                                .build();
                        return audioData;
                    })
                    .collect(Collectors.toList());
            post.getAudioMetadata().addAll(newAudioDataList);
        }

        if (updatePostDTO.removeAudioIds().isEmpty() == false) {
            post.getAudioMetadata()
                    .removeIf(audioData -> updatePostDTO.removeAudioIds().contains(audioData.getId()));
        }

        postRepository.save(post);
    }

    @Transactional
    @Override
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

}
