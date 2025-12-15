package com.alececco.y.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.alececco.y.dto.audio.AudioDTO;
import com.alececco.y.dto.post.CreatePostDTO;
import com.alececco.y.dto.post.PostDTO;
import com.alececco.y.dto.post.UpdatePostDTO;
import com.alececco.y.models.AudioData;
import com.alececco.y.models.Posts;
import com.alececco.y.models.Users;
import com.alececco.y.repository.AudioDataRepository;
import com.alececco.y.repository.PostRepository;
import com.alececco.y.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final ModelMapper modelMapper;
    private final PostRepository postRepository;
    private final AudioDataRepository audioDataRepository;
    private final UserRepository usersRepository;

    private PostDTO mapToDTO(Posts post) {
        PostDTO postDTO = modelMapper.map(post, PostDTO.class);

        List<AudioDTO> audio = audioDataRepository.findByPostId(post.getId()).stream()
                .map(a -> AudioDTO.builder()
                        .id(a.getId())
                        .url(a.getFilename())
                        .build())
                .toList();
        postDTO.setAudio(audio);

        Users user = post.getUser();
        postDTO.setUserId(user.getId());
        postDTO.setUsername(user.getUsername());

        return postDTO;
    }

    @Override
    public Optional<PostDTO> getPostById(Long id) {
        Optional<Posts> post = postRepository.findById(id);

        if (post.isEmpty()) {
            return Optional.empty();
        }

        PostDTO postDTO = mapToDTO(null);
        return Optional.of(postDTO);
    }

    // TODO temporary: remove as soon as no longer convenient
    @Override
    public List<PostDTO> getAllPosts() {
        List<PostDTO> posts = postRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();

        return posts;
    }

    @Override
    public void createPost(CreatePostDTO postDTO) {
        Posts post = new Posts();
        post.setTitle(postDTO.title());

        Users user = usersRepository.findById(postDTO.userId()).orElseThrow(
            () -> new IllegalArgumentException("User not found with id: " + postDTO.userId()));
        post.setUser(user);

        List<AudioData> audioDataList = postDTO.files().stream()
                .map(audio -> {
                    AudioData audioData = AudioData.builder()
                        .filename(audio.getName())
                        .post(post)
                        .build();
                    return audioData;
                })
                .collect(Collectors.toList());
        post.setAudioData(audioDataList);

        audioDataRepository.storeAll(audioDataList);
        postRepository.save(post);
    }

    @Override
    public void updatePost(UpdatePostDTO updatePostDTO) {
        Posts post = postRepository.findById(updatePostDTO.getId()).orElseThrow(
            () -> new IllegalArgumentException("Post not found with id: " + updatePostDTO.getId())
        );

        if (updatePostDTO.getNewAudios().isEmpty() == false) {
            List<AudioData> newAudioDataList = updatePostDTO.getNewAudios().stream()
                .map(audioDTO -> {
                    AudioData audioData = AudioData.builder()
                        .filename(audioDTO.file().getName())
                        .post(post)
                        .build();
                    return audioData;
                })
                .collect(Collectors.toList());
            audioDataRepository.storeAll(newAudioDataList);
        }

        audioDataRepository.deleteByIds(
            updatePostDTO.getRemoveAudioIds()
        );

        post.setTitle(updatePostDTO.getTitle());

        postRepository.save(post);
    }

    @Override
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

}
