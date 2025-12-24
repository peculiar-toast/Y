package com.alececco.y.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import com.alececco.y.components.PostMapper;
import com.alececco.y.dto.post.CreatePostDTO;
import com.alececco.y.dto.post.PostDTO;
import com.alececco.y.dto.post.UpdatePostDTO;
import com.alececco.y.models.AudioMetadata;
import com.alececco.y.models.Post;
import com.alececco.y.models.UserRole;
import com.alececco.y.models.Users;
import com.alececco.y.repository.PostRepository;
import com.alececco.y.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostMapper postMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PostServiceImpl postService;

    private Users testUser;
    private Post testPost;

    @BeforeEach
    private void setUp() {
        testUser = Users.builder()
                .id(1L)
                .username("Test user")
                .role(UserRole.USER)
                .build();

        testPost = Post.builder()
                .id(1L)
                .title("Test post title")
                .audioMetadata(new ArrayList<>(List.of(
                        Instancio.create(AudioMetadata.class),
                        Instancio.create(AudioMetadata.class))))
                .user(testUser)
                .build();
    }

    @Test
    void getPostById_postExists_returnsPostDTO() {
        Post post = Post.builder()
                .id(1L)
                .title("Test Post")
                .build();

        PostDTO postDTO = new PostDTO();
        postDTO.setId(1L);
        postDTO.setTitle("Test Post");

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postMapper.toDto(post)).thenReturn(postDTO);

        Optional<PostDTO> result = postService.getPostById(1L);

        verify(postMapper).toDto(post);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals("Test Post", result.get().getTitle());
    }

    @Test
    void getPostById_postDoesNotExist_returnsEmptyOptional() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<PostDTO> result = postService.getPostById(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void createPost_validData_savesPost() {
        Users user = Users.builder()
                .id(1L)
                .username("testuser")
                .build();
        List<MultipartFile> files = List.of(
                mock(MultipartFile.class),
                mock(MultipartFile.class));

        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        CreatePostDTO createPostDTO = new CreatePostDTO("New Post", files, 1L);
        postService.createPost(createPostDTO);

        verify(postRepository).save(postCaptor.capture());

        Post savedPost = postCaptor.getValue();
        assertEquals("New Post", savedPost.getTitle());
        assertEquals(user, savedPost.getUser());
        assertEquals(2, savedPost.getAudioMetadata().size());
        assertEquals(files.getLast().getName(),
                savedPost.getAudioMetadata().getLast().getFilename());
    }

    @Test
    void createPost_userDoesNotExist_throwsException() {
        List<MultipartFile> files = List.of(mock(MultipartFile.class));

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        CreatePostDTO createPostDTO = new CreatePostDTO("New Post", files, 1L);

        assertThrows(EntityNotFoundException.class, () -> {
            postService.createPost(createPostDTO);
        });

        verifyNoInteractions(postRepository);
    }

    @Test
    void updatePost_postExists_updatesTitle() {
        UpdatePostDTO updatePostDTO = new UpdatePostDTO("Updated title", List.of(), List.of());

        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);

        when(postRepository.findById(anyLong())).thenReturn(Optional.of(testPost));

        postService.updatePost(testPost.getId(), updatePostDTO);

        verify(postRepository).save(postCaptor.capture());

        Post capturedPost = postCaptor.getValue();

        assertEquals(updatePostDTO.title(), capturedPost.getTitle());
        assertEquals(testPost.getAudioMetadata(), capturedPost.getAudioMetadata());
    }

    @Test
    void updatePost_postExists_addsAudios() {
        List<MultipartFile> newAudios = List.of(
                mock(MultipartFile.class),
                mock(MultipartFile.class));
        UpdatePostDTO updatePostDTO = new UpdatePostDTO(null, newAudios, List.of());

        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);

        when(postRepository.findById(anyLong())).thenReturn(Optional.of(testPost));

        postService.updatePost(testPost.getId(), updatePostDTO);

        verify(postRepository).findById(anyLong());
        verify(postRepository).save(postCaptor.capture());

        Post capturedPost = postCaptor.getValue();

        assertEquals(4, capturedPost.getAudioMetadata().size());
        assertEquals(testPost.getTitle(), capturedPost.getTitle());
        assertEquals(
                newAudios.getLast().getName(),
                capturedPost.getAudioMetadata().getLast().getFilename());
    }

    @Test
    void updatePost_postDoesNotExist_throwsException() {
        UpdatePostDTO updatePostDTO = new UpdatePostDTO("Updated title", List.of(), List.of());

        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            postService.updatePost(1L, updatePostDTO);
        });
        verify(postRepository).findById(anyLong());
        verify(postRepository, never()).save(any(Post.class));
    }
}