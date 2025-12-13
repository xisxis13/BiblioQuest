package prj.books_teamB_spring.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import prj.books_teamB_spring.model.Post;
import prj.books_teamB_spring.model.Role;
import prj.books_teamB_spring.model.User;
import prj.books_teamB_spring.repository.PostRepository;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private PostService postService;

    private User testUser;
    private Post testPost;

    @SuppressWarnings("unused")
    @BeforeEach
    void setUp() {
        testUser = new User(
            "testuser",
            "test@example.com",
            "password",
            Role.USER
        );
        testUser.setId(1L);

        testPost = new Post();
        testPost.setId(1L);
        testPost.setContent("Test post content");
        testPost.setUserId(testUser.getId());
        testPost.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void addPost_shouldCreatePostSuccessfully() {
        Post post = new Post();
        post.setContent("Ceci est un test de publication");
        post.setUserId(testUser.getId());

        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        Post result = postService.addPost(post);

        assertNotNull(result);
        assertNotNull(post.getCreatedAt());
        verify(postRepository).save(post);
    }

    @Test
    void getAllPosts_shouldReturnAllPosts() {
        Post post1 = new Post();
        post1.setId(1L);
        post1.setContent("Premier post");

        Post post2 = new Post();
        post2.setId(2L);
        post2.setContent("Deuxième post");

        when(postRepository.findAll()).thenReturn(List.of(post1, post2));

        List<Post> result = postService.getAllPosts();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(postRepository).findAll();
    }

    @Test
    void getPostsByUserId_shouldReturnUserPosts() {
        when(userService.findById(1L)).thenReturn(testUser);
        when(postRepository.findByUserId(1L)).thenReturn(List.of(testPost));

        List<Post> result = postService.getPostsByUserId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getUserId());
        verify(userService).findById(1L);
        verify(postRepository).findByUserId(1L);
    }

    @Test
    void getPostsByUserId_shouldThrowException_whenUserNotFound() {
        when(userService.findById(999L)).thenReturn(null);

        Exception exception = assertThrows(IllegalArgumentException.class, 
            () -> postService.getPostsByUserId(999L));

        assertNotNull(exception);
        assertEquals("User not found with id: 999", exception.getMessage());
        verify(userService).findById(999L);
        verify(postRepository, never()).findByUserId(anyLong());
    }

    @Test
    void getPostById_shouldReturnPost_whenPostExists() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

        Post result = postService.getPostById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test post content", result.getContent());
        verify(postRepository).findById(1L);
    }

    @Test
    void getPostById_shouldThrowException_whenPostNotFound() {
        when(postRepository.findById(999L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, 
            () -> postService.getPostById(999L));

        assertNotNull(exception);
        assertEquals("Post not found with id: 999", exception.getMessage());
        verify(postRepository).findById(999L);
    }

    @Test
    void deletePost_shouldRemovePost_whenPostExists() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

        postService.deletePost(1L);

        verify(postRepository).findById(1L);
        verify(postRepository).deleteById(1L);
    }

    @Test
    void deletePost_shouldThrowException_whenPostNotFound() {
        when(postRepository.findById(999L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, 
            () -> postService.deletePost(999L));

        assertNotNull(exception);
        assertEquals("Post not found with id: 999", exception.getMessage());
        verify(postRepository).findById(999L);
        verify(postRepository, never()).deleteById(anyLong());
    }
}