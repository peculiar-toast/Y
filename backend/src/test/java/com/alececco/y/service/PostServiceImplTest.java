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
import org.springframework.mock.web.MockMultipartFile;
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
import com.alececco.y.testfactory.MultipartFileTestFactory;
import com.alececco.y.testfactory.PostTestFactory;
import com.alececco.y.testfactory.UserTestFactory;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostMapper postMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AudioService audioService;

    @InjectMocks
    private PostServiceImpl postService;

    private Users testUser;
    private Post testPost;

    @BeforeEach
    void setUp() {
        testUser = UserTestFactory.validUser();
        testPost = PostTestFactory.withAudios(2);
    }

    @Test
    void getPostById_postExists_returnsPostDTO() {
        Post post = PostTestFactory.validPost();
        PostDTO postDTO = new PostDTO();
        postDTO.setId(post.getId());
        postDTO.setTitle(post.getTitle());

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postMapper.toDto(post)).thenReturn(postDTO);

        Optional<PostDTO> result = postService.getPostById(1L);

        assertTrue(result.isPresent());
        assertEquals(postDTO, result.get());

        verify(postRepository).findById(1L);
        verify(postMapper).toDto(post);
    }

    @Test
    void getPostById_postDoesNotExist_returnsEmptyOptional() {
        when(postRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<PostDTO> result = postService.getPostById(99L);

        assertTrue(result.isEmpty());

        verify(postRepository).findById(99L);
        verifyNoInteractions(postMapper);
    }

    @Test
    void createPost_validData_createsPostAndDelegatesAudio() {
        List<MultipartFile> files = MultipartFileTestFactory.multiple(2);
        CreatePostDTO createDto = new CreatePostDTO("testTitle", files, 1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);

        postService.createPost(createDto);

        verify(userRepository).findById(1L);
        verify(audioService).addAudios(any(Post.class), eq(files));
        verify(postRepository).save(postCaptor.capture());

        Post savedPost = postCaptor.getValue();

        assertEquals("testTitle", savedPost.getTitle());
        assertEquals(testUser, savedPost.getUser());
    }

    @Test
    void createPost_userDoesNotExist_throwsException() {
        CreatePostDTO createPostDTO = new CreatePostDTO("New Post", List.of(), 1L);

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            postService.createPost(createPostDTO);
        });

        verify(userRepository).findById(1L);
        verifyNoInteractions(postRepository, audioService);
    }

    @Test
    void updatePost_postExists_updatesTitleOnly() {
        UpdatePostDTO updatePostDTO = new UpdatePostDTO("Updated title", null, null);

        when(postRepository.findById(anyLong())).thenReturn(Optional.of(testPost));

        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);

        postService.updatePost(testPost.getId(), updatePostDTO);

        verify(postRepository).findById(testPost.getId());
        verify(postRepository).save(postCaptor.capture());
        verifyNoInteractions(audioService);

        Post capturedPost = postCaptor.getValue();

        assertEquals(updatePostDTO.title(), capturedPost.getTitle());
        assertSame(testPost.getAudioMetadata(), capturedPost.getAudioMetadata());
    }

    @Test
    void updatePost_postExists_addsAudiosOnly() {
        List<MultipartFile> newAudios = MultipartFileTestFactory.multiple(2);
        UpdatePostDTO updatePostDTO = new UpdatePostDTO(null, newAudios, null);

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);

        postService.updatePost(1L, updatePostDTO);

        verify(postRepository).findById(1L);
        verify(audioService).addAudios(any(Post.class), eq(newAudios));
        verify(postRepository).save(postCaptor.capture());

        Post capturedPost = postCaptor.getValue();

        assertEquals(testPost.getTitle(), capturedPost.getTitle());
    }

    @Test
    void updatePost_postExists_updatesTitleAndAddsAudios() {
        List<MultipartFile> files = MultipartFileTestFactory.multiple(2);

        UpdatePostDTO dto = new UpdatePostDTO("updatedTitle", files, null);

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);

        postService.updatePost(1L, dto);

        verify(postRepository).findById(1L);
        verify(audioService).addAudios(any(Post.class), eq(files));
        verify(postRepository).save(postCaptor.capture());

        Post savedPost = postCaptor.getValue();

        assertEquals("updatedTitle", savedPost.getTitle());
    }

    @Test
    void updatePost_postDoesNotExist_throwsException() {
        UpdatePostDTO updatePostDTO = new UpdatePostDTO("Updated title", null, null);

        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> postService.updatePost(1L, updatePostDTO));

        verify(postRepository).findById(anyLong());
        verifyNoInteractions(audioService);
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void updatePost_titleEmpty_throwsException() {
        UpdatePostDTO updatePostDTO = new UpdatePostDTO("", null, null);

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

        assertThrows(IllegalArgumentException.class,
                () -> postService.updatePost(1L, updatePostDTO));

        verifyNoInteractions(audioService);
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void updatePost_titleBlank_throwsException() {
        UpdatePostDTO updatePostDTO = new UpdatePostDTO("       ", null, null);

        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

        assertThrows(IllegalArgumentException.class,
                () -> postService.updatePost(1L, updatePostDTO));

        verifyNoInteractions(audioService);
        verify(postRepository, never()).save(any(Post.class));
    }
}